package com.gaaji.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaaji.chat.controller.dto.RoomResponseDto;
import com.gaaji.chat.domain.User;
import com.gaaji.chat.domain.chatroom.ChatRoom;
import com.gaaji.chat.domain.chatroom.ChatRoomMember;
import com.gaaji.chat.domain.chatroom.ChatRoomMemberRepository;
import com.gaaji.chat.domain.post.Joonggo;
import com.gaaji.chat.domain.post.Post;
import com.gaaji.chat.execption.*;
import com.gaaji.chat.repository.ChatRoomRepository;
import com.gaaji.chat.repository.PostRepository;
import com.gaaji.chat.repository.UserRepository;
import com.gaaji.chat.service.dto.ChatCreatedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoonggoChatServiceImpl implements JoonggoChatService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Transactional
    public RoomResponseDto createDuoChatRoom(String buyerId, String joonggoId) {
        User buyer = userRepository.findById(buyerId).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(joonggoId).orElseThrow(PostNotFoundException::new);
        if(!(post instanceof Joonggo)) throw new PostNotJoonggoException();
        ChatRoom duoChatRoom = chatRoomRepository.save(ChatRoom.createDuoChatRoom());
        chatRoomMemberRepository.save(ChatRoomMember.create(post.getOwner(), duoChatRoom));
        chatRoomMemberRepository.save(ChatRoomMember.create(buyer, duoChatRoom));
        try {
            String body = new ObjectMapper().writeValueAsString(ChatCreatedEventDto.create(duoChatRoom));
            kafkaTemplate.send("chat-chatRoomCreated", body);
        } catch (JsonProcessingException e) {
            throw new InternalServerException();
        }
        return RoomResponseDto.of(duoChatRoom);
    }
}

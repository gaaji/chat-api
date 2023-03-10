package com.gaaji.chat.service;

import com.gaaji.chat.controller.dto.UserRoomSaveRequestDto;
import com.gaaji.chat.controller.dto.UserRoomResponseDto;
import com.gaaji.chat.domain.chatroom.ChatRoom;
import com.gaaji.chat.domain.User;
import com.gaaji.chat.domain.chatroom.ChatRoomMember;
import com.gaaji.chat.execption.NotYourUserRoomException;
import com.gaaji.chat.execption.ChatRoomNotFoundException;
import com.gaaji.chat.execption.UserNotFoundException;
import com.gaaji.chat.execption.GroupChatMemberNotFoundException;
import com.gaaji.chat.repository.ChatRoomRepository;
import com.gaaji.chat.repository.UserRepository;
import com.gaaji.chat.repository.GroupChatMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class GroupChatMemberServiceImpl implements GroupChatMemberService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final GroupChatMemberRepository groupChatMemberRepository;

    @Override
    @Transactional
    public UserRoomResponseDto saveForJoining(String userId, UserRoomSaveRequestDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);
        ChatRoomMember chatRoomMember = groupChatMemberRepository.save(ChatRoomMember.create(UUID.randomUUID().toString(), user, chatRoom));
        return UserRoomResponseDto.of(chatRoomMember);
    }

    @Override
    @Transactional
    public void delete(String userId, String userRoomId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoomMember chatRoomMember = groupChatMemberRepository.findById(userRoomId).orElseThrow(GroupChatMemberNotFoundException::new);
        if(!chatRoomMember.getMember().getId().equals(user.getId())) throw new NotYourUserRoomException();
        groupChatMemberRepository.delete(chatRoomMember);
    }

    @Override
    public UserRoomResponseDto findByUserRoomId(String userId, String userRoomId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoomMember chatRoomMember = groupChatMemberRepository.findById(userRoomId).orElseThrow(GroupChatMemberNotFoundException::new);
        if(!chatRoomMember.getMember().getId().equals(user.getId())) throw new NotYourUserRoomException();
        return UserRoomResponseDto.of(chatRoomMember);
    }
}

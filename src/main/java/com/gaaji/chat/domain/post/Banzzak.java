package com.gaaji.chat.domain.post;

import com.gaaji.chat.domain.chatroom.ChatRoom;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DiscriminatorValue("banzzak")
public class Banzzak extends Post {
    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Override
    public List<ChatRoom> getChatRooms() {
        ArrayList<ChatRoom> chatRooms = new ArrayList<>();
        chatRooms.add(chatRoom);
        return chatRooms;
    }
}

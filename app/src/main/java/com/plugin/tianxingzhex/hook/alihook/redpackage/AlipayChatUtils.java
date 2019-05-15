package com.plugin.tianxingzhex.hook.alihook.redpackage;



import com.plugin.tianxingzhex.beans.ChatMessage;
import com.plugin.tianxingzhex.beans.GroupChatMessage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AlipayChatUtils {

    public static List<ChatMessage> parserSingleChatMessage(List<Object> msgs)  {
        if (msgs == null || msgs.size() == 0) return null;
        List<ChatMessage> chatMessageList = new ArrayList<>(msgs.size());
        ChatMessage chatMessage;
        Field fields[] = msgs.get(0).getClass().getFields();
        for (Object msg : msgs) {
            chatMessage = new ChatMessage();
            Field tempField;
            for (Field field : fields) {
                try {
                    tempField = chatMessage.getClass().getDeclaredField(field.getName());
                    tempField.setAccessible(true);
                    tempField.set(chatMessage, field.get(msg));
                } catch (NoSuchFieldException e) {
                    continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            chatMessageList.add(chatMessage);
        }
        return chatMessageList;
    }

    public static List<GroupChatMessage> parserGroupChatMessage(List<Object> msgs) {
        if (msgs == null || msgs.size() == 0) return null;
        List<GroupChatMessage> groupChatMessageList = new ArrayList<>(msgs.size());
        GroupChatMessage chatMessage;
        Field fields[] = msgs.get(0).getClass().getFields();
        for (Object msg : msgs) {
            chatMessage = new GroupChatMessage();
            Field tempField;
            for (Field field : fields) {
                try {
                    tempField = chatMessage.getClass().getDeclaredField(field.getName());
                    tempField.setAccessible(true);
                    tempField.set(chatMessage, field.get(msg));
                } catch (NoSuchFieldException e) {
                    continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            groupChatMessageList.add(chatMessage);
        }
        return groupChatMessageList;
    }

//    public static List<FriendRequest> parserFriendRequest(List friendRequests) {
//        if (friendRequests == null || friendRequests.size() == 0) return null;
//        List<FriendRequest> friendRequestList = new ArrayList<>(friendRequests.size());
//        FriendRequest friendRequest;
//        Field fields[] = friendRequests.get(0).getClass().getFields();
//        for (Object msg : friendRequests) {
//            friendRequest = new FriendRequest();
//            Field tempField;
//            for (Field field : fields) {
//                try {
//                    tempField = friendRequest.getClass().getDeclaredField(field.getName());
//                    tempField.setAccessible(true);
//                    tempField.set(friendRequest, field.get(msg));
//                } catch (NoSuchFieldException e) {
//                    continue;
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//            friendRequestList.add(friendRequest);
//        }
//        return friendRequestList;
//    }
}
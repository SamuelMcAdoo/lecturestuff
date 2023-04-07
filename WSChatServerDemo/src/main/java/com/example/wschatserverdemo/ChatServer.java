package com.example.wschatserverdemo;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value="/ws/{roomID}") // websocket endpoint
public class ChatServer {

    private Map<String, String> usernames = new HashMap<String, String>();
    private static Map<String,String> roomList = new HashMap<String,String>();
    @OnOpen
    public void open(@PathParam("roomID") String roomID, Session session) throws IOException, EncodeException {
        session.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(Server ): Welcome to the chat room. Please state your username to begin.\"}");
        roomList.put(session.getId(),roomID); // associate userID with roomID in hashMap. (different userIDs can yield same roomID)

    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        String userId = session.getId();
        String roomID = roomList.get(userId);
        if (usernames.containsKey(userId)) {
            String username = usernames.get(userId);
            usernames.remove(userId);
            for (Session peer : session.getOpenSessions()){ //broadcast this person left the server
                if(roomList.get(peer.getId()).equals(roomID)) {
                    peer.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(Server): " + username + " left the chat room.\"}");
                }
            }
        }
    }

    @OnMessage
    public void handleMessage(String comm, Session session) throws IOException, EncodeException {
        String userID = session.getId(); // users' ID
        String roomID = roomList.get(userID); // specific room
        JSONObject jsonmsg = new JSONObject(comm); // grab string from message json
        String type = jsonmsg.getString("type");
        String message = jsonmsg.getString("msg");

        // look for refresh type messages..:
        if(type.equals("refresh")){

        }
        // look for chat type messages..:
        else if(type.equals("chat")) {
            if (usernames.containsKey(userID)) { // not their first message
                String username = usernames.get(userID);
                System.out.println(username);
                // for peer in all peers...:
                for (Session peer : session.getOpenSessions()) {
                    // only if they're in our room! Secret!
                    if (roomList.get(peer.getId()).equals(roomID)) {
                        peer.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(" + username + "): " + message + "\"}");
                    }
                }
            } else { //first message is their username
                usernames.put(userID, message);
                session.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(Server ): Welcome, " + message + "!\"}");
                for (Session peer : session.getOpenSessions()) {
                    // only print out to users with same roomID as the user that sends the message, excluding user.
                    if ((!peer.getId().equals(userID)) && (roomList.get(peer.getId()).equals(roomID))) {
                        peer.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(Server): " + message + " joined the chat room.\"}");
                    }
                }
            }
        }

    }

}

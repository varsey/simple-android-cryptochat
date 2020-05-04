package com.example.simplechat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    Integer usersInChat = 1;
    String my_name = "";
    Server server;
    RecyclerView chatWindow;
    Button sendButton;
    EditText inputMessage;
    TextView userCounter;
    MessageController controller;
    Consumer receiveConsumer;
    Consumer userEnterToChat;
    Consumer userLeaveChat;

    protected void getUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ваш ник");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                my_name = input.getText().toString();
                server.sendName(my_name);
            }
        });
        builder.show();
    }

    private void updateUsersCount() {
        userCounter.setText("Пользователей в чате: " + usersInChat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiveConsumer = new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        controller.addMessage(
                                new MessageController.Message(
                                        pair.second,
                                        pair.first,
                                        false
                                )
                        );
                    }
                });
            }
        };

        userEnterToChat = new Consumer<Pair<String, Integer>>() {
            @Override
            public void accept(final Pair<String, Integer> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usersInChat = pair.second;
                        Toast toast = Toast.makeText(
                                getApplicationContext(),
                                "Пользователь " + pair.first + " вошёл в чат",
                                Toast.LENGTH_SHORT
                        );
                        toast.show();
                        updateUsersCount();
                    }
                });
            }
        };

        userLeaveChat = new Consumer<Pair<String, Integer>>() {
            @Override
            public void accept(final Pair<String, Integer> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usersInChat = pair.second;
                        Toast toast = Toast.makeText(
                                getApplicationContext(),
                                "Пользователь " + pair.first + " вышел из чата",
                                Toast.LENGTH_SHORT
                        );
                        toast.show();
                        updateUsersCount();
                    }
                });
            }
        };

        chatWindow = findViewById(R.id.chatWindow);
        sendButton = findViewById(R.id.sendMessage);
        inputMessage = findViewById(R.id.inputMessage);
        userCounter = findViewById(R.id.usersCount);

        controller = new MessageController();
        controller
                .setIncomingLayout(R.layout.incoming_message)
                .setOutgoingLayout(R.layout.message)
                .setMessageTextId(R.id.message)
                .setMessageTimeId(R.id.date)
                .setUserNameId(R.id.nickname)
                .appendTo(chatWindow, this);

        server = new Server(receiveConsumer, userEnterToChat, userLeaveChat);
        try {
            server.connect();
            updateUsersCount();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputMessage.getText().toString();
                controller.addMessage(
                        new MessageController.Message(
                                text,
                                my_name,
                                true
                        )
                );

                inputMessage.setText("");
                server.sendMessage(text);
            }
        });

        getUserName();
    }
}

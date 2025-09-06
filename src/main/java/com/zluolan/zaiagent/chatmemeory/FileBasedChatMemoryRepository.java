package com.zluolan.zaiagent.chatmemeory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.util.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件持久化的 ChatMemoryRepository 实现
 * 使用 Kryo 序列化/反序列化 List<Message>
 */
public class FileBasedChatMemoryRepository implements ChatMemoryRepository {

    private final String BASE_DIR;
    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    public FileBasedChatMemoryRepository(String dir) {
        Assert.hasText(dir, "base dir cannot be null or empty");
        this.BASE_DIR = dir;
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    @Override
    public List<String> findConversationIds() {
        File baseDir = new File(BASE_DIR);
        File[] files = baseDir.listFiles((d, name) -> name.endsWith(".kryo"));
        List<String> ids = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                ids.add(name.substring(0, name.length() - 5)); // 去掉 .kryo
            }
        }
        return ids;
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        File file = getConversationFile(conversationId);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Input input = new Input(new FileInputStream(file))) {
            return kryo.readObject(input, ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        Assert.notNull(messages, "messages cannot be null");
        Assert.noNullElements(messages, "messages cannot contain null elements");

        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, new ArrayList<>(messages));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}

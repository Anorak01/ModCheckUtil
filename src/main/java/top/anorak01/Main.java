package top.anorak01;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.io.*;
import java.util.Properties;

public class Main {
    public static Map<String, String> modlist_w_checksums = new HashMap<>();
    public static void main(String[] args) {
        System.out.println("Processing!");

        File folder = new File(".");

        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        String jarFileName = jarFile.getName();
        for (File mod : Objects.requireNonNull(folder.listFiles())) {

            if (mod.isFile() && !mod.getName().equals(jarFileName) && !mod.getName().equals("modlist.txt")) {

                String checksum = makeChecksum(mod);
                modlist_w_checksums.put(mod.getName(), checksum);
            }
        }
        writeModlist();
        System.out.println(modlist_w_checksums);
        System.out.println("Finished!");
    }

    private static String makeChecksum(File file) {
        byte[] data;
        byte[] hash;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            hash = MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String checksum = new BigInteger(1, hash).toString(16);
        return checksum;
    }

    private static void writeModlist() {
        File modfile = new File("modlist.txt");
        try {
            if (modfile.createNewFile()){
                System.out.println("Created modfile.txt");
            } else {
                System.out.println("modfile exists");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Properties props = new Properties();
        props.putAll(modlist_w_checksums);

        try (OutputStream output = Files.newOutputStream(modfile.toPath())){
            props.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
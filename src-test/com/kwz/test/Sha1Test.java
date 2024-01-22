package com.kwz.test;

import java.security.MessageDigest;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.kwz.entity.User;

import sun.misc.BASE64Encoder;

public class Sha1Test {
    public static void main(String args[]) throws Exception {
        // Security.addProvider(new
        // org.bouncycastle.jce.provider.BouncyCastleProvider());
        // MessageDigest sha = MessageDigest.getInstance("sha-256");
        // byte[] data1 = "admin".getBytes();
        // byte[] salt = "admin@email".getBytes();
        // sha.reset();
        // sha.update(salt);
        // byte[] msgDigest = sha.digest(data1);
        // String sDigest = byteToBase64(msgDigest);
        //
        // System.out.println("--- Message Digest ---" + sDigest);
        PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
        User userDetails = new User("admin", "admin@email", "ROLE_ADMIN", "d1a25936996731a880371b48d7533e44731d467cb16dacd6a40dbc534030ab1b", 1306329201476L);
        boolean passed = passwordEncoder.isPasswordValid(userDetails.getPassword(), "admin", userDetails.getSalt());
        String sDigest = passwordEncoder.encodePassword("admin", userDetails.getSalt());
        System.out.println("--- Message Digest ---" + sDigest + " " + passed + " " + userDetails.getSalt());

        userDetails = new User("userAdmin", "useradmin@email", "ROLE_USER_ADMIN", "1ccc5fd4fdd2f3a54eeb5dffa276cb3ec2b144c48b3c8a4fcfee759d0e702f7b", 1306329201476L);
        passed = passwordEncoder.isPasswordValid(userDetails.getPassword(), "userAdmin", userDetails.getSalt());
        sDigest = passwordEncoder.encodePassword("userAdmin", userDetails.getSalt());
        System.out.println("--- Message Digest ---" + sDigest + " " + passed + " " + userDetails.getSalt());

    }

    public static String byteToBase64(byte[] data) {
        BASE64Encoder endecoder = new BASE64Encoder();
        return endecoder.encode(data);
    }
}

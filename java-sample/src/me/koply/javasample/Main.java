package me.koply.javasample;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Token: ");
        new SampleBot(scanner.nextLine()).run();
    }

}
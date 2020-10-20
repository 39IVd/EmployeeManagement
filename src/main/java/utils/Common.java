package utils;

import java.util.Scanner;

public class Common {
    static Scanner scan = new Scanner(System.in);
    public static boolean isStringInt(String input) {
        // 문자열이 정수인지 판단
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
            return false;
        }
    }
    public static void exitProgram() {
        System.out.println("프로그램을 종료하시겠습니까? 1. 예 2. 아니오\n");
        String num = scan.nextLine().trim();
        switch (num) {
            case "1":
                System.out.println("프로그램을 종료합니다\n");
                System.exit(0);
                break;
            default:
                return;
        }
    }
    public static void clearScreen() {
        for (int i = 0; i < 80; i++)
            System.out.println("");
    }
}

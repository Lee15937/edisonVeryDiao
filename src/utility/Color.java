/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

/**
 *
 * @author kosoo
 */
public enum Color {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    BRIGHTBLUE("\u001B[34;1m"),
    CYAN("\u001b[36m"),
    MAGENTA("\u001B[35m"),
    BRIGHTMAGENTA("\u001B[35;1m"),
    WHITE("\u001B[37m"),
    BRIGHTRED("\u001B[31;1m"),
    BRIGHTGREEN("\u001B[32;1m"),
    BRIGHTWHITE("\u001B[37;1m"),
    BRIGHTYELLOW("\u001B[33;1m");

    private final String code;

    Color(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}

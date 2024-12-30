package ru.yandex.practicum.filmorate.exception;

public class NegativeCountException extends RuntimeException{
    public NegativeCountException(String message){
        super(message);
    }
}

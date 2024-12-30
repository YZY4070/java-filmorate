package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user){
        return userStorage.create(user);
    }

    public User update(User newUser){
        return userStorage.update(newUser);
    }

    public User getUserById(Long id){
        return userStorage.getUserById(id);
    }

    public Collection<User> getAllUsers(){
        return userStorage.getAllUsers();
    }

    public void addFriend(Long id, Long friendId){
        userStorage.addFriend(id, friendId);
        log.info("Друг добавлен!");
    }

    public void deleteFriend(Long id, Long friendId){
        userStorage.deleteFriend(id, friendId);
        log.info("Друг удален!");
    }

    public Collection<User> getFriends(Long id){
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long anotherId){
        return userStorage.getCommonFriends(id, anotherId);
    }
}

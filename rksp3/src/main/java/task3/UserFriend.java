package task3;

import io.reactivex.rxjava3.core.Observable;

import java.util.Random;

public class UserFriend {
    private int userId;
    private int friendId;

    public UserFriend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public int getUserId() {
        return userId;
    }

    public int getFriendId() {
        return friendId;
    }

    @Override
    public String toString() {
        return "UserFriend{userId=" + userId + ", friendId=" + friendId + '}';
    }

    // Массив объектов UserFriend с случайными данными
    private static UserFriend[] userFriends;

    // Инициализация массива UserFriend
    static {
        Random random = new Random();
        userFriends = new UserFriend[100];
        for (int i = 0; i < userFriends.length; i++) {
            int userId = random.nextInt(10) + 1; // userId от 1 до 10
            int friendId = random.nextInt(10) + 1; // friendId от 1 до 10
            userFriends[i] = new UserFriend(userId, friendId);
        }
    }

    // Функция getFriends
    public static Observable<UserFriend> getFriends(int userId) {
        return Observable.fromArray(userFriends)
                .filter(uf -> uf.getUserId() == userId);
    }

    public static void main(String[] args) {
        Random random = new Random();

        // Массив случайных userId
        Integer[] userIds = new Integer[10];
        for (int i = 0; i < userIds.length; i++) {
            userIds[i] = random.nextInt(10) + 1; // userId от 1 до 10
        }

        // Создаем поток из массива userIds
        Observable<Integer> userIdStream = Observable.fromArray(userIds);

        // Преобразуем поток userId в поток объектов UserFriend
        userIdStream.flatMap(UserFriend::getFriends)
                .subscribe(uf -> System.out.println("UserFriend: " + uf));
    }
}

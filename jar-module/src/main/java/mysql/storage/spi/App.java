package mysql.storage.spi;
import java.time.LocalDate;
import java.util.List;

import mysql.storage.entity.User;
import mysql.storage.repo.UserRepo;

public class App {

    public static void main(String[] args) {
        UserRepo repo = new UserRepo();

        repo.updateCredentials("p.esco", "password");
//        User u = users.get(0);
//        System.out.println(u.toString() + users.toString());
//        System.out.println("User [id=" + u.getId() + ", email=" + u.getEmail() + ", nickName=" + u.getNickName() + ", firstName=" + u.getFirstName()
//				+ ", lastName=" + u.getLastName() + ", birthDate=" + u.getBirthDate() + ", city=" + u.getCity() + ", province=" + u.getProvince()
//				+ ", address=" + u.getAddress() +  ", password=" + u.getPassword() + "]");
    }
}

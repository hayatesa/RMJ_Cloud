package rmj.cloud.eureka;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.util.Scanner;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

    public static void main(String[] args) {
        // 读取控制台输入，决定使用哪个profiles
        Scanner scan = new Scanner(System.in);
        System.out.print("Input profile name: ");
        String profiles = scan.nextLine();
        new SpringApplicationBuilder(EurekaApplication.class).profiles(profiles).run(args);
    }
}

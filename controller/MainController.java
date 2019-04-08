package com.example.myfiles.controller;

import com.example.myfiles.domain.Browse;
import com.example.myfiles.domain.Message;
import com.example.myfiles.domain.Users;
import com.example.myfiles.other.Duanxin;
import com.example.myfiles.other.Now;
import com.example.myfiles.repositories.BrowseRepository;
import com.example.myfiles.repositories.MessageRepository;
import com.example.myfiles.repositories.UsersRepository;
import com.example.myfiles.websocket.WebSocketServer_chat;
import com.example.myfiles.websocket.WebSocketServer_login;
import com.github.qcloudsms.SmsStatusPullReplyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
public class MainController {
    @Autowired
    private BrowseRepository browseRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/addBrowseRecord")
    @ResponseBody
    public void addBrowseRecord(Browse browse,HttpSession session){
        browse.setDate(Now.getNowTime("yyyy年MM月dd日"));
        browse.setTime(Now.getNowTime("HH:mm:ss"));
        browse.setUserid(session.getAttribute("myfiles_user").toString());
        browse.setOperation("登录");
        browseRepository.save(browse);
    }

    @PostMapping("/getBrowseRecord")
    @ResponseBody
    public List<Browse> getBrowseRecord(@RequestParam("date") String date,
                                        @RequestParam("a") int a,
                                        @RequestParam("b") int b){
        return browseRepository.findOneDay(date,a,b);
    }
    @PostMapping("/getBrowseRecords")
    @ResponseBody
    public List<Object> getBrowseRecords(){
        return browseRepository.findAllDays();
    }


    @PostMapping("/sendDuanXin")        //------------------------发送短信
    @ResponseBody
    public boolean sendDuanXin(@RequestParam("type") String type,
                               @RequestParam("time") String time,
                               @RequestParam("phoneNumber") String phoneNumber){
        String code = new Random().nextInt(9000)+1000+"";
        if (type.equals("登录")){
            if(usersRepository.findByPhoneNumber(phoneNumber)==null){
                Users users = new Users(phoneNumber,code,new Date());
                usersRepository.save(users);
            }else{
                usersRepository.setCode(code,phoneNumber);
                usersRepository.setCodeTime(new Date(),phoneNumber);
            }
        }else if(type.equals("注册")){
            if(usersRepository.findByPhoneNumber(phoneNumber)!=null&&usersRepository.findByPhoneNumber(phoneNumber).getPassword()!=null){
                return false;
            }
            if(usersRepository.findByPhoneNumber(phoneNumber)!=null){
                usersRepository.setCode(code,phoneNumber);
                usersRepository.setCodeTime(new Date(),phoneNumber);
            }else {
                Users users = new Users(phoneNumber,code,new Date());
                usersRepository.save(users);
            }
        }else if(type.equals("密码重置")){
            if(usersRepository.findByPhoneNumber(phoneNumber)==null){
                return false;
            }
            usersRepository.setCode(code,phoneNumber);
            usersRepository.setCodeTime(new Date(),phoneNumber);
        }else return false;
        return new Duanxin().sendDuanXin(type,code,time,phoneNumber);
    }
    @PostMapping("/loginWithCode")      //------------------------短信验证码登录
    @ResponseBody
    public String loginWithCode(@RequestParam("phoneNumber")String phoneNumber,
                                @RequestParam("code")String code,
                                HttpSession session){
        Users users = usersRepository.findByPhoneNumberAndCode(phoneNumber,code);
        if(users==null){
            return  "验证码错误";
        }else {
            usersRepository.setCode(null,users.getPhoneNumber());
            session.setAttribute("myfiles_user",users.getPhoneNumber());
            return users.getPhoneNumber();
        }
    }
    @PostMapping("/loginWithPassword")      //------------------------密码登录
    @ResponseBody
    public String loginWithPassword(@RequestParam("phoneNumber")String phoneNumber,
                                    @RequestParam("password")String password,
                                    HttpSession session){
        Users users = usersRepository.findByPhoneNumberAndPassword(phoneNumber,password);
        if(users==null){
            return  "用户名或密码错误";
        }else {
            usersRepository.setCode(null,users.getPhoneNumber());
            session.setAttribute("myfiles_user",users.getPhoneNumber());
            return users.getPhoneNumber();
        }
    }
    @PostMapping("/register")       //------------------------注册
    @ResponseBody
    public String register(@RequestParam("phoneNumber")String phoneNumber,
                           @RequestParam("code")String code,
                           @RequestParam("password")String password){
        Users users = usersRepository.findByPhoneNumberAndCode(phoneNumber,code);
        if(usersRepository.findByPhoneNumber(phoneNumber)!=null&&usersRepository.findByPhoneNumber(phoneNumber).getPassword()!=null){
            return "该手机号已被注册";
        }else{
            if(users!=null){
                usersRepository.setPassword(password,phoneNumber);
                usersRepository.setCode(null,users.getPhoneNumber());
                return phoneNumber;
            }else return "验证码错误";
        }
    }

    @PostMapping("/forgetPassword")     //------------------------忘记密码
    @ResponseBody
    public String forgetPassword(@RequestParam("phoneNumber")String phoneNumber,
                                 @RequestParam("code")String code,
                                 @RequestParam("password")String password){
        Users users = usersRepository.findByPhoneNumberAndCode(phoneNumber,code);
        if(usersRepository.findByPhoneNumber(phoneNumber)==null){
            return "该手机号不存在";
        }else{
            if(users!=null){
                usersRepository.setPassword(password,phoneNumber);
                usersRepository.setCode(null,users.getPhoneNumber());
                return phoneNumber;
            }else return "验证码错误";
        }
    }
    @PostMapping("/getLogin")       //获取登录信息
    @ResponseBody
    public String getLogin(HttpSession session){
        String user = "";
        if(session.getAttribute("myfiles_user")!=null){
            user = session.getAttribute("myfiles_user").toString();
        }
        return user;
    }
    @PostMapping("/setLogin")       //获取登录信息
    @ResponseBody
    public void setLogin(@RequestParam("userid")String userid,
                           HttpSession session){
        if(session.getAttribute("myfiles_user")==null||!session.getAttribute("myfiles_user").equals(userid)){
            session.setAttribute("myfiles_user",userid);
        }
    }
    @PostMapping("/exitLogin")      //退出登录
    @ResponseBody
    public void exitLogin(HttpSession session){
        session.removeAttribute("myfiles_user");
    }


    @PostMapping("/sendMsg")
    @ResponseBody
    public void sendMsg(@RequestParam("msg") String msg){
        try {
            WebSocketServer_login.sendInfo("{\"type\":\"adminmsg\",\"msg\":\""+msg+"\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/sendTheseMsg")
    @ResponseBody
    public void sendTheseMsg(@RequestParam("msg") String msg,
                             @RequestParam("userid")String userid){
        try {
            WebSocketServer_login.sendInfo("{\"type\":\"adminmsg\",\"msg\":\""+msg+"\"}",userid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/chatting")     //聊天室发消息
    @ResponseBody
    public void chatting(@RequestParam("message")String msg,
                         @RequestParam("userid")String uid){
        try {
            Message message = new Message(uid,msg);
            message.setTime(Now.getNowTime("yyyy-MM-dd HH:mm:ss"));
            messageRepository.save(message);
            WebSocketServer_chat.sendInfo(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/getLoginUser")
    @ResponseBody
    public CopyOnWriteArraySet<WebSocketServer_login> getLoginUser(){
        return WebSocketServer_login.getWebSocketSet();
    }

    @PostMapping("/getAllUsersInfo")
    @ResponseBody
    public List<Users> getAllUsersInfo(){
        return usersRepository.findAll();
    }

    @PostMapping("/setUserCode")
    @ResponseBody
    public void setUsersCode(@RequestParam("code") String code,
                             @RequestParam("phoneNumber") String phoneNumber){
        usersRepository.setCode(code,phoneNumber);
        usersRepository.setCodeTime(new Date(),phoneNumber);
    }

    @PostMapping("/setUserPassword")
    @ResponseBody
    public void setUserPassword(@RequestParam("password") String password,
                             @RequestParam("phoneNumber") String phoneNumber){
        usersRepository.setPassword(password,phoneNumber);
        usersRepository.setCode(null,phoneNumber);
    }
    @PostMapping("/adduser")       //------------------------注册
    @ResponseBody
    public int adduser(Users user){
        Users users = usersRepository.findByPhoneNumber(user.getPhoneNumber());
        if(users==null){
            usersRepository.save(user);
            return 1;
        }else return 0;
    }
    @PostMapping("/deleteUser")
    @ResponseBody
    public void setUserPassword(@RequestParam("phoneNumber") String phoneNumber){
        usersRepository.deleteByPhoneNumber(phoneNumber);
    }
    @PostMapping("/getusermsg")
    @ResponseBody
    public String getusermsg(@RequestParam("phoneNumber") String phoneNumber,
                                               @RequestParam("day") int day){
        String s = new Duanxin().getOnePhoneMessage(phoneNumber,day);
        return s;
    }
    @PostMapping("/getMessages")
    @ResponseBody
    public List<Message> getMessages(@RequestParam("a") int a,
                              @RequestParam("b") int b){
        return messageRepository.getMessages(a,b);
    }
}

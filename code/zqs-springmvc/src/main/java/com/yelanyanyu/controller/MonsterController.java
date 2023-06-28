package com.yelanyanyu.controller;

import com.yelanyanyu.entity.Monster;
import com.yelanyanyu.service.MonsterService;
import com.yelanyanyu.zqsspringmvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Controller
public class MonsterController {
    @Autowired
    private MonsterService monsterService;
    @RequestMapping(value = "/monster/show")
    public String showMonster(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("showMonster...");
        return "";
    }



    @RequestMapping(value = "/monster/list/json")
    @ResponseBody
    public List<Monster> listMonster(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(monsterService);
        List<Monster> monsters = monsterService.listMonsters();
        for (Monster monster : monsters) {
            System.out.println(monster);
        }
        return monsters;
    }

    @RequestMapping(value = "/monster/find")
    public String findMonster(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "username") String name,
                              String job, Integer id) {
        System.out.println("findMonster...");
        System.out.println(name);
        System.out.println(job);
        System.out.println(id);
        return "";
    }
}

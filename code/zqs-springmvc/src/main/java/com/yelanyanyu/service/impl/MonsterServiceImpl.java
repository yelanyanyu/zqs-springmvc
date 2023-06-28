package com.yelanyanyu.service.impl;

import com.yelanyanyu.entity.Monster;
import com.yelanyanyu.service.MonsterService;
import com.yelanyanyu.zqsspringmvc.annotation.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Service
public class MonsterServiceImpl implements MonsterService {
    @Override
    public List<Monster> listMonsters() {
        ArrayList<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster(100, "牛魔王", "芭蕉扇", 500));
        monsters.add(new Monster(200, "蜘蛛精", "吐口水", 200));
        return monsters;
    }
}

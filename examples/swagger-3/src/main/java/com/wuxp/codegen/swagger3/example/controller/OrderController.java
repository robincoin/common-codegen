package com.wuxp.codegen.swagger3.example.controller;


import com.wuxp.codegen.swagger3.example.domain.Order;
import com.wuxp.codegen.swagger3.example.domain.User;
import com.wuxp.codegen.swagger3.example.evt.CreateOrderEvt;
import com.wuxp.codegen.swagger3.example.evt.QueryOrderEvt;
import com.wuxp.codegen.swagger3.example.resp.PageInfo;
import com.wuxp.codegen.swagger3.example.resp.ServiceQueryResponse;
import com.wuxp.codegen.swagger3.example.resp.ServiceResponse;
import com.wuxp.codegen.swagger3.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;



@RestController
@RequestMapping(value = "/order")
public class OrderController extends BaseController<String> {

    static Map<Long, User> users = Collections.synchronizedMap(new HashMap<Long, User>());

    @Autowired
    private UserService userService;

    @GetMapping(value = {"getOrder"})
    public List<Order> getOrder(String[] names, List<Integer> ids, Set<Order> moneys) {
        return Collections.EMPTY_LIST;
    }

    @RequestMapping(method = RequestMethod.GET)
    public PageInfo<Order> queryOrder(@RequestBody QueryOrderEvt evt) {
        return new PageInfo<Order>();
    }



    @PostMapping(value = {"queryOrder2"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ServiceQueryResponse<Order> queryOrder2(@RequestParam(name = "order_id", required = false) Long oderId, String sn) {

        return new ServiceQueryResponse<>();
    }

    @PostMapping(value = {"queryPage"}, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ServiceResponse<PageInfo<Order>> queryPage(String id) {

        return new ServiceResponse<>();
    }


    @GetMapping(value = {"createOrder"})
    public ServiceResponse<Long> createOrder(/*@RequestBody*/ CreateOrderEvt evt) {

        return new ServiceResponse<>();
    }


    @PostMapping(value = {"hello"})
    public ServiceResponse hello() {

        return new ServiceResponse<>();
    }

}
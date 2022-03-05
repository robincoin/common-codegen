package com.wuxp.codegen.swagger3.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

      import java.util.List;
      import com.wuxp.codegen.swagger3.ExampleDto;
      import com.wuxp.codegen.swagger3.evt.ExampleDTO;
      import java.util.Map;

    /**
     * 接口：GET
     * example_cms
    **/

  @FeignClient(
        path = "/example_cms" 
  )
public interface ExampleFeignClient{

    /**
      * 1:Http请求方法：GET
      * 2:返回值在java中的类型为：List
      * 3:返回值在java中的类型为：Integer
     **/
      @GetMapping(value = "get_num" )
    List<Integer>  getNums (
  Integer num
  );
    /**
      * 1:Http请求方法：GET
      * 2:返回值在java中的类型为：List
      * 3:返回值在java中的类型为：Map
      * 4:返回值在java中的类型为：Integer
      * 5:返回值在java中的类型为：String
     **/
      @GetMapping(value = "get_maps" )
    List<Map<Integer,String>>  getMaps (
  Integer num
  );
    /**
      * 1:Http请求方法：GET
      * 2:返回值在java中的类型为：Map
      * 3:返回值在java中的类型为：String
      * 4:返回值在java中的类型为：Integer
     **/
      @GetMapping(value = "get_map" )
    Map<String,Integer>  getMap (
  );
    /**
      * 1:Http请求方法：GET
      * 2:返回值在java中的类型为：Map
      * 3:返回值在java中的类型为：String
      * 4:返回值在java中的类型为：List
      * 5:返回值在java中的类型为：Boolean
     **/
      @GetMapping(value = "get_map_2" )
    Map<String,List<Boolean>>  getMap2 (
  );
    /**
      * 1:Http请求方法：GET
      * 2:返回值在java中的类型为：Map
      * 3:返回值在java中的类型为：String
      * 4:返回值在java中的类型为：List
      * 5:返回值在java中的类型为：Boolean
     **/
      @GetMapping(value = "get_map_3/{id}" )
    Map<String,List<Boolean>>  getMap3 (
          @PathVariable(name = "id" )  String id
  );
    /**
      * 1:Http请求方法：GET
      * 2:返回值在java中的类型为：Map
      * 3:返回值在java中的类型为：String
      * 4:返回值在java中的类型为：List
      * 5:返回值在java中的类型为：Boolean
     **/
      @GetMapping(value = "get_map_/" )
    Map<String,List<Boolean>>  getMap4 (
  ExampleDTO dto
  );
    /**
      * 1:Http请求方法：GET
      * 2:返回值在java中的类型为：Map
      * 3:返回值在java中的类型为：String
      * 4:返回值在java中的类型为：List
      * 5:返回值在java中的类型为：Boolean
     **/
      @GetMapping(value = "get_map_5" )
    Map<String,List<Boolean>>  getMap5 (
  ExampleDto dto
  );
}

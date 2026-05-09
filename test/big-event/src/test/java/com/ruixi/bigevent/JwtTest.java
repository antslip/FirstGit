package com.ruixi.bigevent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testGen(){
        Map<String,Object> claim = new HashMap<>();
        claim.put("id",1);
        claim.put("username","yangmengzhe");
        //生成JWT代码
        String token = JWT.create()
                .withClaim("user", claim)//添加载荷
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))//设置过期时间
                .sign(Algorithm.HMAC256("ruixi"));//指定算法，设置密钥
        System.out.println(token);
    }


    //@Test
    public void testParse(){
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJ1c2VyIjp7ImlkIjoxLCJ1c2VybmFtZSI6InlhbmdtZW5nemhlIn0sImV4cCI6MTc3NjI5NjE3Nn0" +
                ".QrmdGnxfOzp0VATPpzqvwVcjcKODRrbwIfi35pu2oSg";
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("ruixi")).build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        Map<String, Claim> claims = decodedJWT.getClaims();
        System.out.println(claims.get("user"));
    }
}

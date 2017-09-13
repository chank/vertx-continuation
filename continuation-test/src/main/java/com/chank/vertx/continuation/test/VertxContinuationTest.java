/*
 * Copyright 2017 方里权 (Chank)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chank.vertx.continuation.test;

import com.chank.vertx.continuation.Sync;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.io.InputStream;

import static jdk.internal.org.objectweb.asm.Opcodes.*;


/**
 * @author Chank
 */
public class VertxContinuationTest {

    private ClassNode cn;

    public static void test() throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.createHttpClient().getNow("www.baidu.com","/", resp -> {
            System.out.println("Gota response " + resp.statusCode());
            resp.bodyHandler(body -> {
                System.out.println("Gota data " + body.toString("ISO-8859-1"));
                vertx.close();
            });
        });
    }

    public static void awaitTest() throws Exception {
        Vertx vertx = Vertx.vertx();
        HttpClientResponse response = Sync.awaitEvent(h -> vertx.createHttpClient().getNow("www.baidu.com","/", h));
        System.out.println("Got response " + response.statusCode());
        Buffer body = Sync.awaitEvent(h -> response.bodyHandler(h));
        System.out.println("Got data " + body.toString("ISO-8859-1"));
        vertx.close();
    }

    public static void main(String[] args) throws Exception {
        test();
        awaitTest();
//        ClassNode cn = new ClassNode();
//        cn.version = V1_5;
//        cn.access = ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE;
//        cn.name = "pkg/Comparable";
//        cn.superName = "java/lang/Object";
//        cn.interfaces.add("pkg/Mesurable");
//        cn.fields.add(new FieldNode(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I", null, -1));
//        cn.fields.add(new FieldNode(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I", null, 1));
//        cn.fields.add(new FieldNode(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I", null, 1));
//        cn.methods.add(new MethodNode(ACC_PUBLIC + ACC_ABSTRACT, "compareTo", "(Ljava/lang/Object;)I", null, null));
    }

}

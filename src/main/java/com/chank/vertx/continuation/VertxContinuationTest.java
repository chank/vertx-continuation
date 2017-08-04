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

package com.chank.vertx.continuation;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author Chank
 */
public class VertxContinuationTest {

    public void test() throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.createHttpClient().getNow("www.baidu.com","/", resp -> {
            System.out.println("Gota response " + resp.statusCode());
            resp.bodyHandler(body -> {
                System.out.println("Gota data " + body.toString("ISO-8859-1"));
            });
        });
    }

    public void awaitTest() throws Exception {
        Vertx vertx = Vertx.vertx();
        HttpClientResponse response = Sync.awaitEvent(h -> vertx.createHttpClient().getNow("www.baidu.com","/", h));
        System.out.println("Got response " + response.statusCode());
        Buffer body = Sync.awaitEvent(h -> response.bodyHandler(h));
        System.out.println("Got data " + body.toString("ISO-8859-1"));
    }

    public static void main(String[] args) throws IOException {
        ClassVisitor cl=new ClassVisitor(Opcodes.ASM4) {

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                System.out.println("Visiting class: " + name);
                System.out.println("Class Major Version: " + version);
                System.out.println("Super class: "+superName);
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public void visitOuterClass(String owner, String name, String desc) {
                System.out.println("Outer class: " + owner);
                super.visitOuterClass(owner, name, desc);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                System.out.println("Annotation: " + desc);
                return super.visitAnnotation(desc, visible);
            }

            @Override
            public void visitAttribute(Attribute attr) {
                System.out.println("Class Attribute: " + attr.type);
                super.visitAttribute(attr);
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                System.out.println("Inner Class: " + innerName + " defined in " + outerName);
                super.visitInnerClass(name, outerName, innerName, access);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                System.out.println("Field: " + name + " " + desc + " value: " + value);
                return super.visitField(access, name, desc, signature, value);
            }

            @Override
            public void visitEnd() {
                System.out.println("Method ends here");
                super.visitEnd();
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                System.out.println("Method: " + name + " " + desc);
                return super.visitMethod(access, name, desc, signature, exceptions);
            }

            @Override
            public void visitSource(String source, String debug) {
                System.out.println("Source: " + source);
                super.visitSource(source, debug);
            }


        };
        InputStream in = VertxContinuationTest.class.getResourceAsStream("/com/chank/vertx/continuation/VertxContinuationTest.class");
        ClassReader classReader=new ClassReader(in);
        classReader.accept(cl, 0);
    }

}

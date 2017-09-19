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

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.internal.org.objectweb.asm.util.ASMifier;
import jdk.internal.org.objectweb.asm.util.Textifier;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;
import jdk.internal.org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

/**
 * @author Chank
 */
public final class ContinuationTransformer implements ClassFileTransformer {

    private static final ContinuationTransformer INSTANCE = new ContinuationTransformer();

    public static ContinuationTransformer getInstance() {
        return INSTANCE;
    }

    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals("com/chank/vertx/continuation/test/VertxContinuationTest")) {
            try {
                ClassNode classNode = new ClassNode();
                ClassReader classReader = new ClassReader(classfileBuffer);
                classReader.accept(classNode, 0);

                Iterator<MethodNode> methodNodeIterator = classNode.methods.iterator();
                while (methodNodeIterator.hasNext()) {
                    MethodNode methodNode = methodNodeIterator.next();
                    if (methodNode.name.equals("awaitTest")) {
                        Iterator<AbstractInsnNode> insnNodeIterator = methodNode.instructions.iterator();
                        AbstractInsnNode abstractInsnNode = null;
                        while (insnNodeIterator.hasNext()) {
                            AbstractInsnNode insnNode = insnNodeIterator.next();
                            if (insnNode instanceof MethodInsnNode) {
                                MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                                if (methodInsnNode.owner.equals("com/chank/vertx/continuation/Sync") && methodInsnNode.name.equals("awaitEvent")) {
                                    abstractInsnNode = methodInsnNode.getPrevious();
                                    insnNodeIterator.remove();
                                    while (insnNodeIterator.hasNext()) {
                                        insnNodeIterator.next();
                                        insnNodeIterator.remove();
                                    }
                                }
                            }
                        }
                        if (abstractInsnNode != null) {
                            methodNode.instructions.remove(abstractInsnNode.getPrevious().getPrevious().getPrevious());
                            methodNode.instructions.remove(abstractInsnNode.getPrevious().getPrevious());
                            methodNode.instructions.remove(abstractInsnNode.getPrevious());
                            methodNode.instructions.remove(abstractInsnNode);
                        }
                        LabelNode labelNode = new LabelNode();
                        methodNode.instructions.add(labelNode);
                        methodNode.instructions.add(new LineNumberNode(51, labelNode));
                        methodNode.instructions.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.add(new LabelNode());
                    }
                }

                ClassWriter classWriter = new ClassWriter(0);
                classNode.accept(classWriter);

                classNode.accept(new TraceClassVisitor(new PrintWriter(System.out)));

                return classWriter.toByteArray();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return classfileBuffer;
    }

}

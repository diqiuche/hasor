/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.dataql.domain;
import net.hasor.dataql.domain.inst.CompilerStack;
import net.hasor.dataql.domain.inst.InstQueue;
/**
 * 二元运算表达式
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DyadicExpression extends Expression {
    private Expression fstExpression;   //第一个表达式
    private String     dyadicSymbol;    //运算符
    private Expression secExpression;   //第二个表达式
    public DyadicExpression(Expression fstExpression, String dyadicSymbol, Expression secExpression) {
        super();
        this.fstExpression = fstExpression;
        this.dyadicSymbol = dyadicSymbol;
        this.secExpression = secExpression;
    }
    //
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        //
        this.fstExpression.doCompiler(queue, stackTree);
        this.secExpression.doCompiler(queue, stackTree);
        queue.inst(DO, this.dyadicSymbol);
    }
}
/*
 * Copyright 2016 The Simple File Server Authors
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

package org.sfs.rx;

public class Holder3<A, B, C> {

    public A value0;
    public B value1;
    public C value2;

    public Holder3() {
    }

    public Holder3(A value0, B value1, C value2) {
        this.value0 = value0;
        this.value1 = value1;
        this.value2 = value2;
    }

    public Holder3<A, B, C> value0(A value) {
        this.value0 = value;
        return this;
    }

    public Holder3<A, B, C> value1(B value) {
        this.value1 = value;
        return this;
    }

    public Holder3<A, B, C> value2(C value) {
        this.value2 = value;
        return this;
    }

    public A value0() {
        return value0;
    }

    public B value1() {
        return value1;
    }

    public C value2() {
        return value2;
    }

    @Override
    public String toString() {
        return "Holder3{" +
                "value0=" + value0 +
                ", value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }
}

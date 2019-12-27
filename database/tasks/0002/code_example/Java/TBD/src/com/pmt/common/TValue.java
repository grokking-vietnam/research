package com.pmt.common;

import org.apache.thrift.*;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.protocol.*;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by minhtam on 19/12/2019
 */
public class TValue implements TBase<TValue, TValue._Fields>, Serializable, Cloneable  {

    private static final TStruct STRUCT_DESC = new TStruct("TValue");
    private static final TField FLAG_FIELD_DESC = new TField("flag", (byte)8, (short)1);
    private static final TField DATA_FIELD_DESC = new TField("data", (byte)11, (short)2);
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap();
    public int flag;
    public ByteBuffer data;
    private static final int __FLAG_ISSET_ID = 0;
    private byte __isset_bitfield = 0;
    private TValue._Fields[] optionals;
    public static final Map<TValue._Fields, FieldMetaData> metaDataMap;

    public TValue() {
        this.optionals = new TValue._Fields[]{TValue._Fields.FLAG, TValue._Fields.DATA};
    }

    public TValue(TValue var1) {
        this.optionals = new TValue._Fields[]{TValue._Fields.FLAG, TValue._Fields.DATA};
        this.__isset_bitfield = var1.__isset_bitfield;
        this.flag = var1.flag;
        if (var1.isSetData()) {
            this.data = var1.data;
        }

    }

    public TValue deepCopy() {
        return new TValue(this);
    }

    public void clear() {
        this.setFlagIsSet(false);
        this.flag = 0;
        this.data = null;
    }

    public int getFlag() {
        return this.flag;
    }

    public TValue setFlag(int var1) {
        this.flag = var1;
        this.setFlagIsSet(true);
        return this;
    }

    public void unsetFlag() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }

    public boolean isSetFlag() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }

    public void setFlagIsSet(boolean var1) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, var1);
    }

    public byte[] getData() {
        this.setData(TBaseHelper.rightSize(this.data));
        return this.data == null ? null : this.data.array();
    }

    public ByteBuffer bufferForData() {
        return this.data;
    }

    public TValue setData(byte[] var1) {
        this.setData(var1 == null ? (ByteBuffer)null : ByteBuffer.wrap(var1));
        return this;
    }

    public TValue setData(ByteBuffer var1) {
        this.data = var1;
        return this;
    }

    public void unsetData() {
        this.data = null;
    }

    public boolean isSetData() {
        return this.data != null;
    }

    public void setDataIsSet(boolean var1) {
        if (!var1) {
            this.data = null;
        }

    }

    public void setFieldValue(TValue._Fields var1, Object var2) {
        switch(var1) {
            case FLAG:
                if (var2 == null) {
                    this.unsetFlag();
                } else {
                    this.setFlag((Integer)var2);
                }
                break;
            case DATA:
                if (var2 == null) {
                    this.unsetData();
                } else {
                    this.setData((ByteBuffer)var2);
                }
        }

    }

    public Object getFieldValue(TValue._Fields var1) {
        switch(var1) {
            case FLAG:
                return this.getFlag();
            case DATA:
                return this.getData();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TValue._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case FLAG:
                    return this.isSetFlag();
                case DATA:
                    return this.isSetData();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TValue ? this.equals((TValue)var1) : false;
        }
    }

    public boolean equals(TValue var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetFlag();
            boolean var3 = var1.isSetFlag();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (this.flag != var1.flag) {
                    return false;
                }
            }

            boolean var4 = this.isSetData();
            boolean var5 = var1.isSetData();
            if (var4 || var5) {
                if (!var4 || !var5) {
                    return false;
                }

                if (!this.data.equals(var1.data)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TValue var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetFlag()).compareTo(var1.isSetFlag());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetFlag()) {
                    var4 = TBaseHelper.compareTo(this.flag, var1.flag);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                var4 = Boolean.valueOf(this.isSetData()).compareTo(var1.isSetData());
                if (var4 != 0) {
                    return var4;
                } else {
                    if (this.isSetData()) {
                        var4 = TBaseHelper.compareTo(this.data, var1.data);
                        if (var4 != 0) {
                            return var4;
                        }
                    }

                    return 0;
                }
            }
        }
    }

    public TValue._Fields fieldForId(int var1) {
        return TValue._Fields.findByThriftId(var1);
    }

    public void read(TProtocol var1) throws TException {
        ((SchemeFactory)schemes.get(var1.getScheme())).getScheme().read(var1, this);
    }

    public void write(TProtocol var1) throws TException {
        ((SchemeFactory)schemes.get(var1.getScheme())).getScheme().write(var1, this);
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TValue(");
        boolean var2 = true;
        if (this.isSetFlag()) {
            var1.append("flag:");
            var1.append(this.flag);
            var2 = false;
        }

        if (this.isSetData()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("data:");
            if (this.data == null) {
                var1.append("null");
            } else {
                var1.append(this.data);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    private void writeObject(ObjectOutputStream var1) throws IOException {
        try {
            this.write(new TCompactProtocol(new TIOStreamTransport(var1)));
        } catch (TException var3) {
            throw new IOException(var3);
        }
    }

    private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
        try {
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(var1)));
        } catch (TException var3) {
            throw new IOException(var3);
        }
    }

    static {
        schemes.put(StandardScheme.class, new TValue.TValueStandardSchemeFactory());
        schemes.put(TupleScheme.class, new TValue.TValueTupleSchemeFactory());
        EnumMap var0 = new EnumMap(TValue._Fields.class);
        var0.put(TValue._Fields.FLAG, new FieldMetaData("flag", (byte)2, new FieldValueMetaData((byte)8)));
        var0.put(TValue._Fields.DATA, new FieldMetaData("data", (byte)2, new FieldValueMetaData((byte)11, "TCompactValue")));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TValue.class, metaDataMap);
    }

    private static class TValueTupleScheme extends TupleScheme<TValue> {
        private TValueTupleScheme() {
        }

        public void write(TProtocol var1, TValue var2) throws TException {
            TTupleProtocol var3 = (TTupleProtocol)var1;
            BitSet var4 = new BitSet();
            if (var2.isSetFlag()) {
                var4.set(0);
            }

            if (var2.isSetData()) {
                var4.set(1);
            }

            var3.writeBitSet(var4, 2);
            if (var2.isSetFlag()) {
                var3.writeI32(var2.flag);
            }

            if (var2.isSetData()) {
                var3.writeBinary(var2.data);
            }

        }

        public void read(TProtocol var1, TValue var2) throws TException {
            TTupleProtocol var3 = (TTupleProtocol)var1;
            BitSet var4 = var3.readBitSet(2);
            if (var4.get(0)) {
                var2.flag = var3.readI32();
                var2.setFlagIsSet(true);
            }

            if (var4.get(1)) {
                var2.data = var3.readBinary();
                var2.setDataIsSet(true);
            }

        }
    }

    private static class TValueTupleSchemeFactory implements SchemeFactory {
        private TValueTupleSchemeFactory() {
        }

        public TValue.TValueTupleScheme getScheme() {
            return new TValue.TValueTupleScheme();
        }
    }

    private static class TValueStandardScheme extends StandardScheme<TValue> {
        private TValueStandardScheme() {
        }

        public void read(TProtocol var1, TValue var2) throws TException {
            var1.readStructBegin();

            while(true) {
                TField var3 = var1.readFieldBegin();
                if (var3.type == 0) {
                    var1.readStructEnd();
                    var2.validate();
                    return;
                }

                switch(var3.id) {
                    case 1:
                        if (var3.type == 8) {
                            var2.flag = var1.readI32();
                            var2.setFlagIsSet(true);
                        } else {
                            TProtocolUtil.skip(var1, var3.type);
                        }
                        break;
                    case 2:
                        if (var3.type == 11) {
                            var2.data = var1.readBinary();
                            var2.setDataIsSet(true);
                        } else {
                            TProtocolUtil.skip(var1, var3.type);
                        }
                        break;
                    default:
                        TProtocolUtil.skip(var1, var3.type);
                }

                var1.readFieldEnd();
            }
        }

        public void write(TProtocol var1, TValue var2) throws TException {
            var2.validate();
            var1.writeStructBegin(TValue.STRUCT_DESC);
            if (var2.isSetFlag()) {
                var1.writeFieldBegin(TValue.FLAG_FIELD_DESC);
                var1.writeI32(var2.flag);
                var1.writeFieldEnd();
            }

            if (var2.data != null && var2.isSetData()) {
                var1.writeFieldBegin(TValue.DATA_FIELD_DESC);
                var1.writeBinary(var2.data);
                var1.writeFieldEnd();
            }

            var1.writeFieldStop();
            var1.writeStructEnd();
        }
    }

    private static class TValueStandardSchemeFactory implements SchemeFactory {
        private TValueStandardSchemeFactory() {
        }

        public TValue.TValueStandardScheme getScheme() {
            return new TValue.TValueStandardScheme();
        }
    }

    public static enum _Fields implements TFieldIdEnum {
        FLAG((short)1, "flag"),
        DATA((short)2, "data");

        private static final Map<String, TValue._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TValue._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return FLAG;
                case 2:
                    return DATA;
                default:
                    return null;
            }
        }

        public static TValue._Fields findByThriftIdOrThrow(int var0) {
            TValue._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TValue._Fields findByName(String var0) {
            return (TValue._Fields)byName.get(var0);
        }

        private _Fields(short var3, String var4) {
            this._thriftId = var3;
            this._fieldName = var4;
        }

        public short getThriftFieldId() {
            return this._thriftId;
        }

        public String getFieldName() {
            return this._fieldName;
        }

        static {
            Iterator var0 = EnumSet.allOf(TValue._Fields.class).iterator();

            while(var0.hasNext()) {
                TValue._Fields var1 = (TValue._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}

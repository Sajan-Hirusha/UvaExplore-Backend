package com.User.middleware;
import com.pgvector.PGvector;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;

public class VectorType implements UserType<float[]> {

    @Override
    public int getSqlType() {
        return SqlTypes.OTHER; // Or use Types.OTHER
    }

    @Override
    public Class<float[]> returnedClass() {
        return float[].class;
    }

    @Override
    public boolean equals(float[] x, float[] y) throws HibernateException {
        return Objects.deepEquals(x, y);
    }

    @Override
    public int hashCode(float[] x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public float[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        PGvector vector = (PGvector) rs.getObject(position);
        return vector != null ? vector.toArray() : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, float[] value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, new PGvector(value));
        }
    }

    @Override
    public float[] deepCopy(float[] value) throws HibernateException {
        if (value == null) return null;
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(float[] value) throws HibernateException {
        return value;
    }

    @Override
    public float[] assemble(Serializable cached, Object owner) throws HibernateException {
        return (float[]) cached;
    }

    @Override
    public float[] replace(float[] original, float[] target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
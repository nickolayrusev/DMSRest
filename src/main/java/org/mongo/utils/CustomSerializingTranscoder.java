package org.mongo.utils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import net.spy.memcached.transcoders.SerializingTranscoder;
/**
 * issue with memcachier
 * https://code.google.com/p/spymemcached/issues/detail?id=155#c2
 * https://code.google.com/p/spymemcached/issues/detail?id=146
 * @author rusev
 *
 */
public class CustomSerializingTranscoder extends SerializingTranscoder{

    @Override
    protected Object deserialize(byte[] bytes) {
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        ObjectInputStream in = null;
        try {
            ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bs) {
                @SuppressWarnings("unchecked")
				@Override
                protected Class<ObjectStreamClass> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                    try {
                        return (Class<ObjectStreamClass>) currentClassLoader.loadClass(objectStreamClass.getName());
                    } catch (Exception e) {
                        return (Class<ObjectStreamClass>) super.resolveClass(objectStreamClass);
                    }
                }
            };
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            closeStream(in);
        }
    }

    private static void closeStream(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
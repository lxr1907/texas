package yuelj.utils.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Kryo序列化和反序列化
 * 
 * @author lilm8
 *
 * @param <T>
 */
public class KryoSerializer<T> {

	private Kryo kryo = new Kryo();

	public byte[] serialize(Object t) throws IOException {
		byte[] buffer = new byte[toByteArray(t).length];
		Output output = new Output(buffer);
		kryo.writeClassAndObject(output, t);
		return output.toBytes();

	}

	public T deserialize(byte[] bytes) {
		Input input = new Input(bytes);
		@SuppressWarnings("unchecked")
		T t = (T) kryo.readClassAndObject(input);
		return t;
	}

	/**
	 * Object to Byte
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	private byte[] toByteArray(Object obj) throws IOException {
		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			oos = null;
			bos.close();
			bos = null;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (oos != null) {
				oos.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
		return bytes;
	}

}

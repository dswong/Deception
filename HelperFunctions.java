import java.io.IOException;
import java.util.Arrays;

public class HelperFunctions {
	/**
	 * Concatenates the length of the given string with the string in bytes[]
	 * Source: http://stackoverflow.com/questions/5368704/appending-a-byte-to-the-end-of-another-byte
	 * @param val
	 * @return
	 */
	public static byte[] appendLength(String val){
		byte[] data = val.getBytes();
		byte[] length = intToBytes(val.length());
		byte[] ret = new byte[length.length + data.length];

		System.arraycopy(length, 0, ret, 0, length.length);
		
		System.arraycopy(data, 0, ret, length.length, data.length);
		
		return ret;
	}
	
	/**
	 * Converts an integer to a byte[] in order to be appended to a packet
	 * Source: http://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java
	 * @param val
	 * @return
	 */
	public static byte[] intToBytes(int val){
		byte[] result = new byte[4];

		result[0] = (byte) (val >> 24);
		result[1] = (byte) (val >> 16);
		result[2] = (byte) (val >> 8);
		result[3] = (byte) (val);

		return result;
	}
	
	/**
	 * Removes the first four bytes of a string
	 * @param val
	 * @return String without the first four bytes
	 */
	public static String removeLength(String val){
		byte[] data = val.getBytes();
		data = Arrays.copyOfRange(data, 4, data.length);
		String ret = new String(data);
		return ret;
	}
	
	/**
	 * Give a String with four bytes representing an integer prepended to it, convert those first
	 * four bytes to an int and return it
	 * Bytes-to-int Source: http://stackoverflow.com/questions/9581530/converting-from-byte-to-int-in-java
	 * @param val
	 * @return
	 */
	public static int getLength(String val){
		byte[] data = val.getBytes();
		int length =(data[0]<<24)&0xff000000|
		       		(data[1]<<16)&0x00ff0000|
		       		(data[2]<< 8)&0x0000ff00|
		       		(data[3]<< 0)&0x000000ff;
		return length - 1;	// -1 to account for the additional newline character appended by the server
	}
	
	/**
	 * Send a String to the given Players
	 * @param val
	 * @param players
	 */
	public static void sendMessage(String val, Player[] players){
		for (Player player: players){
			try {
				player.outToClient.write(HelperFunctions.appendLength(val + '\n'));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Send a String to a player
	 * @param val
	 * @param player
	 */
	public static void sendMessage(String val, Player player){
		try {
			player.outToClient.write(HelperFunctions.appendLength(val + '\n'));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a list of players and an id, return the player with the given id
	 * @param players
	 * @param id
	 * @return
	 */
	public static Player getPlayer(Player[] players, int id){
		for (Player player: players){
			if (player.id == id){
				return player;
			}
		}
		return null;
	}
}

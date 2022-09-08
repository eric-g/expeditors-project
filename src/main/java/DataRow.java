import java.util.List;

/**
 * Model of CSV record
 *
 */
public class DataRow {

	public String firstName;
	public String lastName;
	public String address;
	public String city;
	public String state;
	public String age;
	public String addressCityState;

	public DataRow(List<String> list) {
		super();
		this.firstName = list.get(0);
		this.lastName = list.get(1);
		this.address = normalizeString(list.get(2));
		this.city = normalizeString(list.get(3));
		this.state = list.get(4).toUpperCase();
		this.age = list.get(5);
		this.addressCityState = this.address + this.city + this.state;
	}

	private String normalizeString(String string) {
		string = string.replaceAll("[^\\w\\s]", "");
		String[] split = string.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].substring(0, 1).toUpperCase() + split[i].substring(1);
			sb.append(split[i] + " ");
		}
		return sb.toString().trim();

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAddressCityState() {
		return addressCityState;
	}

	public void setAddressCityState(String addressCityState) {
		this.age = addressCityState;
	}

}

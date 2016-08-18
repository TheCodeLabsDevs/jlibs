package de.tobias.database;

@Nameable("Test")
public class TestClassA {

	@Nameable("FieldA") @Key public String fieldA;
	@Nameable("FieldB") public String fieldB;

	@Override
	public String toString() {
		return "TestClassA [fieldA=" + fieldA + ", fieldB=" + fieldB + "]";
	}

}

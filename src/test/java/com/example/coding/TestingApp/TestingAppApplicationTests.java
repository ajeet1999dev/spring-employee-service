package com.example.coding.TestingApp;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class TestingAppApplicationTests {

	@BeforeEach
	void SetUp(){
		log.info("This Method is run before each test case");
	}

	@AfterEach
	void TearDown(){
		log.info("This Method is run After each test case");
	}

	@BeforeAll
	static void SetUpOnce(){
		log.info("Set up once ....");
	}

	@AfterAll
	static void TearDownOnce(){
		log.info("tear Down ....");
	}

	@Test
//	@Disabled
	void testNumberOne() {
//		log.info("test one is run");
		int a = 5;
		int b = 3;

		int result = addTwoNumber(a,b);
//		Assertions.assertEquals(8,result); // this method is depend on datatype so we used assertJ library instead of this

		Assertions.assertThat(result)
						.isEqualTo(8)
				.isCloseTo(9, Offset.offset(1));

		Assertions.assertThat("Apple")
				.isEqualTo("Apple")
				.startsWith("App")
				.endsWith("le")
				.hasSize(5);
	}

	@Test
//	@DisplayName("This is my test 2")
	void testDivideTwoNumber_whenDenominatorIsZero_ThenArithmeticException(){
		log.info("test two is run");
		int a = 5;
		int b = 0;

//		double result = divideTwoNumbers(a,b);
//		System.out.println(result);
		Assertions.assertThatThrownBy(() -> divideTwoNumbers(a,b))
				.isInstanceOf(ArithmeticException.class)
				.hasMessage("Tried to divide by zero");
	}

	int addTwoNumber(int a, int b){
		return a+b;
	}

	double divideTwoNumbers(int a, int b){
		try{
			return a /b;
		} catch (ArithmeticException e){
			log.error("Arithmetic exception found with: "+e.getLocalizedMessage());
			throw new ArithmeticException("Tried to divide by zero");
		}
	}

}

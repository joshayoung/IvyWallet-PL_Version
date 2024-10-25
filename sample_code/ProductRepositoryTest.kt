
package com.plcoding.testingcourse.core.data

import com.plcoding.testingcourse.core.domain.Product
import com.plcoding.testingcourse.core.domain.ProductRepository

class ProductRepositoryTest: ProductRepository {

    private lateinit var repository: ProductRepositoryImpl
    private lateinit var productApi: productApi
    // private lateinit var analyticsLogger: FirebaseAnalyticsLogger
    private lateinit var analyticsLogger: AnalyticsLogger

    val product = Product(
        id = 1,
        name = "Ice cream",
        price = 5.0
    )
    
    // example for mocking if you do not own the code:
    mockKConstructor(Product::class)
    every { anyConstructed<Product>().name } return "Mocked ice cream"

    @BeforeEach
    fun setUp() {
        productApi = mockk()
        // complete working object with return values
        analyticsLogger = mockk(relaxed = true)
        repository = ProductRepositoryImpl(productApi, analyticsLogger)
    }

    @Test
    fun `Response error, exception is logged`() = runBlocking {
        // this answer does not do anything:
        // 'any()' is a matcher
        every { analyticsLogger.logEvent(any(), any(), any())} answers {
            println("this is a log event")
        }

        coEvery { productApi.purchaseProducts(any()) } throws mockk<HttpException> {
            // httpexception object

            // define how it shoudl behave:
            // every call throws http exception with 404
            every { code() } returns 404
            every { message() } returns "Test message"
        }

        var result = repository.purchaseProducts(listOf())

        
        // we expect a failure:
        assertThat(result.isFailure).isTrue()

        // we can exapect how many calls:
        verify(exactly = 3) {
            analyticsLogger.logEvent(
               "http_error",
               LogParam("code", 404) ,
               LogParam("code", "Test message") 
            )
        }
        
        // we can verify a function was called a specific number of times:
        verify {
            analyticsLogger.logEvent(
               "http_error",
               LogParam("code", 404) ,
               LogParam("code", "Test message") 
            )
        }

    }
}
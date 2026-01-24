package org.sparta.kopring.test

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@Tag(name = "Test", description = "Datadog POC 테스트용 API")
@RestController
@RequestMapping("/test")
class TestController(
    private val testService: TestService,
) {
    // ========== 정상 케이스 ==========

    @Operation(summary = "정상 응답", description = "정상 응답 테스트")
    @GetMapping("/success")
    fun success(): Map<String, Any> =
        mapOf(
            "status" to "ok",
            "message" to "Success response",
            "timestamp" to System.currentTimeMillis(),
        )

    @Operation(summary = "느린 응답", description = "3초 지연 응답 테스트")
    @GetMapping("/slow")
    fun slow(): Map<String, Any> {
        Thread.sleep(3000)
        return mapOf(
            "status" to "ok",
            "message" to "Slow response after 3 seconds",
            "timestamp" to System.currentTimeMillis(),
        )
    }

    // ========== CRITICAL: 즉시 대응 필요 ==========

    @Operation(summary = "[CRITICAL] DB 연결 실패", description = "데이터베이스 연결 장애")
    @GetMapping("/db-connection-fail")
    fun dbConnectionFail(): Nothing = throw RuntimeException("Unable to acquire JDBC Connection: Connection refused to mysql-primary:3306")

    @Operation(summary = "[CRITICAL] 결제 처리 실패", description = "결제 시스템 장애")
    @GetMapping("/payment-fail")
    fun paymentFail(): Nothing = throw RuntimeException("Payment gateway timeout: PG system not responding after 30000ms")

    @Operation(summary = "[CRITICAL] 데이터 정합성 오류", description = "주문-재고 불일치")
    @GetMapping("/data-integrity-error")
    fun dataIntegrityError(): Nothing =
        throw IllegalStateException("Data integrity violation: Order #12345 references non-existent inventory record")

    // ========== HIGH: 주요 기능 장애 ==========

    @Operation(summary = "[HIGH] NPE - 코드 버그", description = "널 체크 누락으로 인한 NPE")
    @GetMapping("/npe")
    fun npe(): Map<String, Any> = testService.processUserData(null)

    @Operation(summary = "[HIGH] 외부 API 타임아웃", description = "외부 서비스 응답 지연")
    @GetMapping("/external-api-timeout")
    fun externalApiTimeout(): Nothing = throw RuntimeException("Connection timed out to external-api.com after 10000ms")

    @Operation(summary = "[HIGH] 인덱스 초과", description = "배열 인덱스 초과 접근")
    @GetMapping("/index-out-of-bounds")
    fun indexOutOfBounds(): Map<String, Any> = testService.getItemAtIndex(100)

    @Operation(summary = "[HIGH] 인증 토큰 만료", description = "JWT 토큰 검증 실패")
    @GetMapping("/auth-token-expired")
    fun authTokenExpired(): Nothing = throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token expired at 2024-01-15T10:30:00Z")

    @Operation(summary = "[HIGH] 동시성 충돌", description = "낙관적 락 실패")
    @GetMapping("/optimistic-lock-fail")
    fun optimisticLockFail(): Nothing = throw RuntimeException("OptimisticLockingFailureException: Row was updated by another transaction")

    // ========== MEDIUM: 부분적 기능 저하 ==========

    @Operation(summary = "[MEDIUM] 캐시 미스", description = "Redis 캐시 조회 실패 - DB fallback")
    @GetMapping("/cache-miss")
    fun cacheMiss(): Nothing = throw RuntimeException("Redis connection failed: READONLY - Failover in progress, falling back to DB")

    @Operation(summary = "[MEDIUM] 파일 업로드 실패", description = "S3 업로드 실패")
    @GetMapping("/s3-upload-fail")
    fun s3UploadFail(): Nothing = throw RuntimeException("AmazonS3Exception: Access Denied for bucket 'product-images'")

    @Operation(summary = "[MEDIUM] 메시지 발행 실패", description = "Kafka 메시지 발행 실패")
    @GetMapping("/kafka-publish-fail")
    fun kafkaPublishFail(): Nothing = throw RuntimeException("KafkaProducerException: Failed to send message to topic 'order-events'")

    @Operation(summary = "[MEDIUM] 비즈니스 로직 오류", description = "할인 계산 오류")
    @GetMapping("/business-logic-error")
    fun businessLogicError(): Map<String, Any> = testService.calculateDiscount(-100)

    // ========== LOW: 경미한 이슈 ==========

    @Operation(summary = "[LOW] 잘못된 사용자 입력", description = "유효하지 않은 ID 형식")
    @GetMapping("/invalid-user-input/{id}")
    fun invalidUserInput(
        @PathVariable id: String,
    ): Nothing {
        if (!id.matches(Regex("^[0-9]+$"))) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format: '$id' - must be numeric")
        }
        throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: $id")
    }

    @Operation(summary = "[LOW] 리소스 없음", description = "존재하지 않는 상품 조회")
    @GetMapping("/product-not-found")
    fun productNotFound(
        @RequestParam(defaultValue = "99999") productId: Long,
    ): Nothing = throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: id=$productId")

    @Operation(summary = "[LOW] 비핵심 기능 실패", description = "추천 알고리즘 실패 - fallback 적용")
    @GetMapping("/recommendation-error")
    fun recommendationError(): Nothing =
        throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Recommendation engine temporarily unavailable - fallback to default list",
        )

    @Operation(summary = "[LOW] Deprecated API 호출", description = "레거시 API 사용")
    @GetMapping("/deprecated")
    fun deprecatedApi(): Nothing =
        throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Deprecated API called: /v1/legacy/users - Please migrate to /v2/users",
        )

    @Operation(summary = "[LOW] Rate Limit 초과", description = "API 호출 제한 초과")
    @GetMapping("/rate-limit-exceeded")
    fun rateLimitExceeded(): Nothing =
        throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded: 100 requests per minute")

    @Operation(summary = "[LOW] 입력값 검증 실패", description = "이메일 형식 오류")
    @GetMapping("/validation-error")
    fun validationError(): Nothing = throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter: 'email' format is incorrect")

    // ========== 복합 시나리오 ==========

    @Operation(summary = "[복합] 검증 누락 버그", description = "입력값 검증 없이 처리하여 발생하는 오류")
    @GetMapping("/missing-validation")
    fun missingValidation(
        @RequestParam(required = false) amount: String?,
    ): Map<String, Any> = testService.processPayment(amount)

    @Operation(summary = "[복합] 서비스 체인 실패", description = "연쇄 서비스 호출 중 실패")
    @GetMapping("/service-chain-fail")
    fun serviceChainFail(): Map<String, Any> = testService.processOrderChain()

    @Operation(summary = "[복합] 반복적 어드민 실수", description = "어드민 사용자의 반복 입력 오류")
    @GetMapping("/admin-repeated-error")
    fun adminRepeatedError(
        @RequestParam(defaultValue = "invalid-json") config: String,
    ): Nothing = throw RuntimeException("JSON parse error: Unexpected character at position 0 - Input: '$config'")
}

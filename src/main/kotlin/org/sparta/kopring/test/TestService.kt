package org.sparta.kopring.test

import org.springframework.stereotype.Service

@Service
class TestService {
    private val items = listOf("apple", "banana", "cherry")

    /**
     * 사용자 데이터 처리 - NPE 발생 가능
     * 문제: null 체크 없이 바로 접근
     */
    fun processUserData(userData: Map<String, Any>?): Map<String, Any> {
        // BUG: null 체크 누락
        val userName = userData!!["name"] as String
        val userAge = userData["age"] as Int

        return mapOf(
            "processed" to true,
            "name" to userName.uppercase(),
            "age" to userAge,
        )
    }

    /**
     * 인덱스로 아이템 조회 - IndexOutOfBoundsException 발생 가능
     * 문제: 범위 체크 없이 바로 접근
     */
    fun getItemAtIndex(index: Int): Map<String, Any> {
        // BUG: 범위 체크 누락
        val item = items[index]

        return mapOf(
            "index" to index,
            "item" to item,
        )
    }

    /**
     * 할인 계산 - 비즈니스 로직 오류
     * 문제: 음수 금액 검증 누락
     */
    fun calculateDiscount(originalPrice: Int): Map<String, Any> {
        // BUG: 음수 가격 검증 누락
        val discountRate = 0.1
        val discountAmount = (originalPrice * discountRate).toInt()
        val finalPrice = originalPrice - discountAmount

        if (finalPrice < 0) {
            throw IllegalArgumentException("Final price cannot be negative: $finalPrice")
        }

        return mapOf(
            "originalPrice" to originalPrice,
            "discountRate" to discountRate,
            "discountAmount" to discountAmount,
            "finalPrice" to finalPrice,
        )
    }

    /**
     * 결제 처리 - 입력값 검증 누락
     * 문제: amount 파라미터 검증 없이 파싱
     */
    fun processPayment(amount: String?): Map<String, Any> {
        // BUG: null 체크 및 숫자 형식 검증 누락
        val parsedAmount = amount!!.toLong()

        if (parsedAmount <= 0) {
            throw IllegalArgumentException("Amount must be positive: $parsedAmount")
        }

        return mapOf(
            "success" to true,
            "amount" to parsedAmount,
            "transactionId" to "TXN-${System.currentTimeMillis()}",
        )
    }

    /**
     * 주문 처리 체인 - 연쇄 서비스 호출 실패
     * 문제: 중간 단계 실패 시 롤백 처리 누락
     */
    fun processOrderChain(): Map<String, Any> {
        // Step 1: 재고 확인
        checkInventory("PROD-001")

        // Step 2: 결제 처리 (여기서 실패)
        processExternalPayment()

        // Step 3: 재고 차감 (실행되지 않음)
        deductInventory("PROD-001")

        return mapOf("status" to "completed")
    }

    private fun checkInventory(productId: String) {
        // 재고 확인 로직
        println("Checking inventory for $productId")
    }

    private fun processExternalPayment() {
        // BUG: 외부 결제 실패 시 이전 단계 롤백 없음
        throw RuntimeException("External payment service failed: Connection reset by peer")
    }

    private fun deductInventory(productId: String) {
        // 재고 차감 로직
        println("Deducting inventory for $productId")
    }
}

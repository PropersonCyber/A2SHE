package main

import (
	"crypto/rand"
	"fmt"
   "time"
	"golang.org/x/crypto/ed25519"
   
)

func main() {
	// 生成密钥对
	publicKey, privateKey, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		fmt.Println("密钥对生成失败：", err)
		return
	}

	message := []byte("01010100101010101010101001010101010101010101010101010100101010101010")

	// 使用私钥进行签名
	signature := ed25519.Sign(privateKey, message)

   var verified bool
   start := time.Now()
   for j := 0; j <10;j++{
      // 使用公钥进行验证
      for i := 0; i < 5000; i++ {
         verified = ed25519.Verify(publicKey, message, signature)
      }
	
   }
   
	
   elapsed := time.Since(start)
   fmt.Printf("Time taken: %s\n", elapsed/10)

	fmt.Println("签名验证结果：", verified)
}

package main

import (
	"crypto/rand"
	"encoding/base64"
	"fmt"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
	"golang.org/x/crypto/ed25519"
)

// SmartContract provides functions for managing an Asset
type EdDSAChaincode struct {
	contractapi.Contract
}

//定义签名请求结构
type SignInput struct {
   Message string `json:"message"`

}

//定义验证请求结构
type VerifyInput struct{
   PublicKey string `json:"publicKey"`
   Message string `json:"message"`
   Signature string `json:"signature"`
}

//定义验证响应结构
type SignResponse struct{
   Signature string `json:"signature"`
}

//verifyResponse 定义验证响应结构
type VerifyResponse struct{
   Result bool `json:"result"`
}

//链码初始化方法
func (cc *EdDSAChaincode) Init(ctx contractapi.TransactionContextInterface) error {
   return nil;
}

//Sign
func (cc *EdDSAChaincode) Sign(ctx contractapi.TransactionContextInterface, input SignInput )(*SignResponse,error ){
    _, privateKey, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		return nil, fmt.Errorf("failed to generate sk: %w", err)
	}

	signature := ed25519.Sign(privateKey, []byte(input.Message))

	response := &SignResponse{
		Signature: base64.StdEncoding.EncodeToString(signature),
	}

	return response, nil
}

// Verify 验证方法
func (cc *EdDSAChaincode) Verify(ctx contractapi.TransactionContextInterface, input VerifyInput) (*VerifyResponse, error) {
	publicKey, err := base64.StdEncoding.DecodeString(input.PublicKey)
	if err != nil {
		return nil, fmt.Errorf("failed to decode pk: %w", err)
	}

	signature, err := base64.StdEncoding.DecodeString(input.Signature)
	if err != nil {
		return nil, fmt.Errorf("failed to decode signature: %w", err)
	}

	result := ed25519.Verify(publicKey, []byte(input.Message), signature)

	response := &VerifyResponse{
		Result: result,
	}

	return response, nil
}



func main() {
   chaincode, err := contractapi.NewChaincode(&EdDSAChaincode{})
	if err != nil {
		fmt.Printf("Error creating EdDSAChaincode: %s", err.Error())
		return
	}

	if err := chaincode.Start(); err != nil {
		fmt.Printf("Error starting EdDSAChaincode: %s", err.Error())
	}
	
}

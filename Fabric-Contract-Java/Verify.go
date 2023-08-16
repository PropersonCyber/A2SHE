package main

import (
	"crypto/rand"
	"encoding/base64"
	"fmt"
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
func (cc *EdDSAChaincode) Sign(ctx contractapi.TransactionContextInterface, input string )(*SignResponse,error ){
	signinput:=SignInput{
		Message: input,
	}
    _, privateKey, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		return nil, fmt.Errorf("failed to generate sk: %w", err)
	}

	signature := ed25519.Sign(privateKey, []byte(signinput.Message))

	response := &SignResponse{
		Signature: base64.StdEncoding.EncodeToString(signature),
	}

	return response, nil
}


func (cc *EdDSAChaincode) VerifyPass(ctx contractapi.TransactionContextInterface,input string) (*VerifyResponse, error){
	publicKey, privateKey, err := ed25519.GenerateKey(rand.Reader)

	signinput:=SignInput{
		Message: input,
	}

	if err != nil {
		return nil, fmt.Errorf("failed to generate key pair: %w", err)
	}

	signature := ed25519.Sign(privateKey, []byte(signinput.Message))
	result := false
	for i := 1; i <= 10; i++ {
		result = ed25519.Verify(publicKey, []byte(signinput.Message), signature)
	}
	response := &VerifyResponse{
		Result: result,
	}

	return response, nil
}


// Verify 验证方法
func (cc *EdDSAChaincode) Verify(ctx contractapi.TransactionContextInterface, inputPk string,inputMsg string,inputSign string) (*VerifyResponse, error) {
	verifyInput:=VerifyInput{
		PublicKey: inputPk,
		Message:inputMsg,
		Signature:inputSign,
	}
	
	publicKey, err := base64.StdEncoding.DecodeString(verifyInput.PublicKey)
	if err != nil {
		return nil, fmt.Errorf("failed to decode pk: %w", err)
	}

	signature, err := base64.StdEncoding.DecodeString(verifyInput.Signature)
	if err != nil {
		return nil, fmt.Errorf("failed to decode signature: %w", err)
	}

	result := ed25519.Verify(publicKey, []byte(verifyInput.Message), signature)

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

package com.example.budget.type;

public enum ConsultingMessage {

  START("새로운 마음가짐으로 지출을 잘 조절해봐요!"),
  HIGH_RISK("현재 예산 소진 속도가 빨라요! 꼭 필요한 곳에 사용했는지 생각해볼까요?"),
  MEDIUM_RISK("현재 적절한 소비를 실천하고 있어요. 조금만 절약해 볼까요?"),
  LOW_RISK("현재 절약을 잘 하고 있어요! 남은 날도 화이팅!")
  ;

  private final String message;

  ConsultingMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

}

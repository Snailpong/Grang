# Grang
## Introduction
Grang은 외국어 듣기 실력의 집중적인 향상을 위해 개발되고 있는 프로젝트입니다.   
이 프로젝트은 영상과 자막의 제공만으로 충분한 음성 강의자료가 될 수 있도록 만드는 것을 목표로 합니다.   

## Implemented Functions
- 저장소로부터 영상, 자막을 불러와서 재생 및 자막을 기반으로 한 재생위치 이동 기능   

## Functions to be implemented
- 문장마다 영상의 음원 재생 후 자막 말해주기 기능
- 학습할 문장 선택 및 제외 기능


## Development Step
~~Step 1. ANS with Kotlin 시도하기. ExoPlayer로 영상과 자막(subrip) 불러와서 재생하기~~   
~~Step 2. 자막을 RecyclerView로 뿌리고, 이동이 가능하게 하기~~   
~~Step 3. Custom Dialog를 이용해 파일 불러오게 하기, 영상과 자막 스크롤링 동기화~~   
~~Step 4. 백그라운드 재생 지원~~   
Step 5. 자막 읽어주기 기능, 읽어주기 패턴 설정   
Step 6. SMI 파일 추가 for Korean   
공통. UI 작업   
...


## Function that want to be added later
- 자막으로부터 단어 추출 및 해당 영상에 나오는 단어 학습 기능
- 유튜브 영상과 자막을 지원 기능
- 음성에 해당하는 부분 외의 자막 제거 기능 (머신러닝 활용)   
- 음성과 자막 모두를 고려한 기능들

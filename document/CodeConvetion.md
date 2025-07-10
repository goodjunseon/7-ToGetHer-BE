## 🤙🏻 Code Convention

> ### 📌 Naming 규칙
> 변수명, 메서드명에는 영어와 숫자만 사용하며, 소문자 카멜 케이스를 기본적으로 사용합니다.
> - 메서드 이름은 동사/전치사로 시작합니다.
> - 클래스명/인터페이스명에는 대문자 카멜표기법을 사용합니다.
>   - good: Store.class (O) 
>   - bad: store.class (X)
> - 클래스명은 명사로 한정합니다.
>   - 한국어 발음대로 표기 금지
>     - good: Sharing.class (O)
>     - bad: Gongyu.class (X)
> - 패키지 이름은 소문자로 구성합니다.

> ### 📌 Declaration 규칙
> - 한줄에 한 문장만을 작성, 문장이 끝나는 ( **;** ) 뒤에는 새 줄을 삽입합니다.
>   - int a = 1; int b = 2; 처럼 작성 금지
> - 배열 선언에 오는 대괄호는 타입 뒤에 선언합니다.
>   - good: String[ ] names;
>   - bad: String names[ ]
>   - 'long'형 값의 마지막에 'L' 붙이기
>   - good: long number = 123L;

> ### 📌 RestAPI 작성 규칙
> - API 엔드 포인트에는 동사 대신 명사를 사용합니다.
>   - HTTP 요청 메서드에 의미가 내포되어 있기 때문에 동작을 작성하지 않아도 됨
>     - good: [GET] /users  [POST] /users
>     - bad: [GET] /getUsers [POST] /createUsers
> - 주소가 길어지는 경우 밑줄( _ )보다는 하이픈( - )을 사용합니다.
> - 엔드포인트 경로에는 소문자만 사용합니다.



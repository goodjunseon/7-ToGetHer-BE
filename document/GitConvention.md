## 🤙🏻 Git Branch & Commit Convention

### 📌 Branch Strategy 

> ### Main Branch
> 1. **main**: 운영(deploy)용 브랜치. 실제 서비스에 배포되는 안정적인 코드
> 2. **dev** : 개발 브랜치, 기능 브랜치들이 병합되는 공간, 테스트 후 main으로 병합합니다.

> ### Feature Branches
>  3. **feat/**: 새로운 기능을 개발할 때 사용
>  4. **fix/**: 버그나 오류를 수정할 때 사용
>  4. **refactor/**: 코드 리팩토링 시 사용

### 📌 Naming Convetion
> **이슈번호/feat/기능명**

---
### 📌 Commit Convention
> 1. 커밋 유형은 아래와 같이 작성합니다.
> 2. 제목과 본문을 빈행으로 분리합니다.
> 3. 제목의 첫 글자는 대문자로 작성하고, 제목 끝에 마침표(.)를 금지한다.

| 커밋 유형 | 의미 |
| --- | --- |
| `Feat` | 새로운 기능 추가 |
| `Fix` | 버그 수정 |
| `Docs` | 문서 수정 |
| `Style` | 코드 formatting, 세미콜론 누락, 코드 자체의 변경이 없는 경우 |
| `Refactor` | 코드 리팩토링 |
| `Test` | 테스트 코드, 리팩토링 테스트 코드 추가 |
| `Comment` | 필요한 주석 추가 및 변경 |
| `Rename` | 파일 또는 폴더 명을 수정하거나 옮기는 작업만인 경우 |
| `Remove` | 파일을 삭제하는 작업만 수행한 경우 |
| `!HOTFIX` | 급하게 치명적인 버그를 고쳐야 하는 경우 |
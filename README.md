# spring-gift-refactoring

## 구현할 기능 목록

### Phase 0: 테스트 코드 작성
- [x] test(category): 카테고리 CRUD API 인수테스트 작성
- [x] test(member): 회원 가입/로그인 API 인수테스트 작성
- [x] test(product): 상품 CRUD API 인수테스트 작성
- [x] test(option): 상품 옵션 API 인수테스트 작성
- [x] test(wish): 위시리스트 API 인수테스트 작성
- [x] test(order): 주문 API 인수테스트 작성

### Phase 1: 스타일 정리
- [ ] style(all): ktlintFormat 자동 포맷 적용
- [ ] style(all): 수동 정리 (네이밍 컨벤션, import 정렬 등)

### Phase 2: 미사용 코드 제거
- [ ] refactor(all): 미사용 import 제거
- [ ] refactor(all): 미사용 변수 및 private 메서드 제거
- [ ] refactor(all): 주석 처리된 레거시 코드 제거

### Phase 3: Controller 내에 있는 Service Layer 추출
- [ ] refactor(category): CategoryService 생성 및 비즈니스 로직 이동
- [ ] refactor(member): MemberService 생성 및 비즈니스 로직 이동
- [ ] refactor(product): ProductService 생성 및 비즈니스 로직 이동
- [ ] refactor(option): OptionService 생성 및 비즈니스 로직 이동
- [ ] refactor(wish): WishService 생성 및 비즈니스 로직 이동
- [ ] refactor(order): OrderService 생성 및 비즈니스 로직 이동

## 구현 전략

### Phase 0: 테스트 코드 작성
- **목적**: 리팩터링 전 현재 동작을 보호하는 안전망 확보
- **접근**: 각 도메인의 Controller API를 대상으로 인수테스트 작성
- **범위**: 정상 요청/응답 흐름(Happy Path) 위주
- **도구**: SpringBootTest + MockMvc
- **순서**: 의존성 적은 도메인부터 (category → member → product → option → wish → order)
- **검증**: `./gradlew test` 전체 통과

### Phase 1: 스타일 정리
- **도구**: ktlint, IDE 포맷터
- **접근**: `./gradlew ktlintFormat` 자동 적용 후 수동 정리
- **주의**: 포맷팅 중 로직 변경 절대 금지
- **검증**: `./gradlew test` 통과 확인

### Phase 2: 미사용 코드 제거
- **접근**: IDE 인스펙션 → git blame 확인 → 제거
- **주의**: TODO/주석 의도 확인 필수, 의도 불분명 시 보류
- **검증**: 빌드 + 테스트 통과

### Phase 3: Controller 내에 있는 Service Layer 추출
- **순서**: 의존성 적은 도메인부터 (category → member → product → option → wish → order)
- **원칙**: Controller는 요청 수신 + 응답 반환만 담당
- **주의**: 로직 이동 시 기능 추가/변경 금지
- **검증**: 각 도메인 추출 후 테스트 통과

## 진행 기록

### 2026-02-26
#### 작업 내용
- 프로젝트 구조 분석 및 리팩터링 준비 작업 완료

#### 의사결정
- **프로젝트 현황 파악**: 전체 41개 소스 파일, 9개 Controller 확인. Service 계층이 전혀 없으며 Controller에 비즈니스 로직(Repository 호출, 검증, 외부 API 호출 등)이 집중되어 있음을 확인
- **CLAUDE.md 작성**: AI 페어 프로그래밍 파트너의 행동 규약 정의. README.md 기반 선 계획 후 실행 원칙, Refactor/Feature 커밋 혼합 금지, 대량 변경 금지, 요청하지 않은 기능 추가 금지 규칙 수립
- **스킬 3종 구성**: readme-management(README 관리), acceptance-test(인수테스트 작성법), refactoring(리팩터링 절차) 스킬을 `.claude/skills/` 하위에 생성
- **Service 추출 순서 결정**: 도메인 간 의존성 분석 결과 category(의존성 없음) → member → product(category 의존) → option(product 의존) → wish(member+product 의존) → order(가장 복잡, 외부 API 의존) 순서로 진행하기로 결정
- **테스트 작성 순서**: Service 추출 순서와 동일하게 의존성 적은 도메인부터 작성

#### 이슈
- 테스트 코드가 전혀 없는 상태 (`src/test/` 하위에 `.gitkeep`만 존재). Phase 0에서 인수테스트를 먼저 확보해야 이후 리팩터링의 안전망이 보장됨
- Order, Auth 도메인은 Kakao 외부 API 의존성이 있어 Mock 처리가 필요

## AI 활용 기록

### 2026-02-26 — 리팩터링 준비 (CLAUDE.md, Skills, README 구성)
- **활용 방식**: 프로젝트 전체 구조 탐색 및 분석, CLAUDE.md 행동 규약 초안 생성, 스킬 문서 3종 작성, README.md 체크리스트 구성
- **AI 산출물 수정 내용**: CLAUDE.md에 "대량 변경 금지", "요청하지 않은 기능 추가 금지" 규칙을 사용자 요청에 따라 추가. readme-management 스킬에 Phase 0(인수테스트) 섹션을 사용자 요청에 따라 보강
- **학습한 내용**: 프로젝트가 Java 21 + Kotlin 1.9.25 혼용 환경이며, Flyway로 DB 마이그레이션을 관리하고, H2 인메모리 DB를 테스트에 활용할 수 있음을 파악

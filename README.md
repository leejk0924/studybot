# StudyBot - Discord Study Time Tracker

Discord를 통해 스터디를 이어가면서, 어떻게 하면 더 큰 동기부여를 이끌어낼 수 있을지 고민했습니다.
그 과정에서 스터디 시간을 측정하고 기록한다면 참여자들이 보다 흥미롭게 스터디에 임할 수 있겠다는 아이디어를 얻었습니다.
이 프로젝트는 단순한 시간 측정을 넘어, 스터디를 더욱 유연하고 지속적으로 이어갈 수 있도록 돕는 것을 목표로 합니다.

## 주요 기능

- Discord 음성 채널 입장/퇴장 시간 자동 추적
- 주간 스터디 시간 랭킹 시스템 (매주 일요일 자동 발표)
- `!!명령어` 기반 직관적인 인터페이스 
  - 사용자가 `총 공부시간`, `오늘의 공부시간`, `상태`, `랭킹` 등을 간단히 조회할 수 있어 편의성과 접근성 강화
- 데이터베이스 기반 안정적인 세션 관리

## 빠른 시작

StudyBot을 실행하기 위해서는 Docker가 필요합니다. 아래 단계를 따라 쉽게 설정할 수 있습니다.

## 설치 및 실행

### 1. 환경 설정

`.env.example`의 설정값들을 입력하고, `.env` 파일을 생성하세요.

```bash
# .env 파일 생성
cp .env.example .env
```

### 2. 실행

`deploy.sh` 스크립트를 사용하여 쉽게 관리할 수 있습니다:

#### 기본 실행
```bash
# 초기 네트워크 설정
./deploy.sh setup

# 데이터베이스 시작
./deploy.sh db-start

# 애플리케이션 시작
./deploy.sh app-start

# 또는 전체 시작 (권장)
./deploy.sh full-start
```

> **주의**: 최초 실행 시 데이터베이스 스키마를 초기화해야 합니다.
> ```bash
> # 데이터베이스 스키마 생성
> docker exec -i studybot-mysql mysql -u ${DB_USERNAME} -p${DB_PASSWORD} ${DB_NAME} < src/main/resources/schema.sql
> ```

#### 관리 명령어
```bash
# 상태 확인
./deploy.sh status

# 로그 확인
./deploy.sh logs

# 애플리케이션 재시작
./deploy.sh app-restart

# 코드 변경 후 재빌드
./deploy.sh app-rebuild
```

#### 정리 명령어
```bash
# 애플리케이션만 중지
./deploy.sh app-stop

# 데이터베이스만 중지
./deploy.sh db-stop

# 전체 중지
./deploy.sh full-stop

# 완전 정리 (이미지 + 네트워크 삭제)
./deploy.sh clean
```

## 관리 및 운영

### 설정 정보

- **Profile**: 환경변수로 설정 (proc | local)
- **Database**: MySQL 8.0 (DB 접속 정보는 .env를 통해 주입)
- **Discord Bot Token**: 환경변수로 주입 (.env를 통해 주입)

### 문제 해결

```bash
# 컨테이너 상태 확인
./deploy.sh status

# 데이터베이스 접속 확인
docker exec studybot-mysql mysql -u ${DB_USERNAME} -p${DB_PASSWORD} ${DB_NAME}

# 앱 재빌드 (코드 변경 시)
./deploy.sh app-rebuild
```

### 데이터 백업

MySQL 데이터는 프로젝트의 `./data/db` 디렉토리에 저장됩니다.

```bash
# 백업
docker exec studybot-mysql mysqldump -u ${DB_USERNAME} -p${DB_PASSWORD} ${DB_NAME} > backup.sql

# 복원
docker exec -i studybot-mysql mysql -u ${DB_USERNAME} -p${DB_PASSWORD} ${DB_NAME} < backup.sql

# 데이터 디렉토리 백업
tar -czf data-backup.tar.gz ./data/db
```

## 참고 자료

### deploy.sh 스크립트 명령어

| 명령어 | 설명 | 사용 시점 |
|--------|------|-----------|
| `setup` | Docker 네트워크 생성 | 최초 설치 시 |
| `db-start` | 데이터베이스만 시작 | DB 단독 관리 시 |
| `db-stop` | 데이터베이스만 중지 | DB 단독 관리 시 |
| `app-start` | 애플리케이션만 시작 | 앱 단독 관리 시 |
| `app-stop` | 애플리케이션만 중지 | 앱 단독 관리 시 |
| `app-restart` | 애플리케이션 재시작 | 설정 변경 후 |
| `app-rebuild` | 앱 재빌드 및 재시작 | 코드 변경 후 |
| `logs` | 애플리케이션 로그 확인 | 디버깅 시 |
| `status` | 컨테이너/네트워크 상태 확인 | 문제 진단 시 |
| `full-start` | 전체 서비스 시작 | 일반적인 시작 |
| `full-stop` | 전체 서비스 중지 | 일반적인 중지 |
| `clean` | 이미지/네트워크 완전 삭제 | 완전 초기화 시 |

### 일반적인 사용법

```bash
# 1. 최초 설치
./deploy.sh full-start

# 2. 코드 수정 후
./deploy.sh app-rebuild

# 3. 문제 발생 시
./deploy.sh status
./deploy.sh logs

# 4. 완전 정리
./deploy.sh clean
```

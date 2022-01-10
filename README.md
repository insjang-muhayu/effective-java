# effective-java

-------------------------------------------------------------------------------------
## 목적
 - 깔끔한 자바 코딩을 위해

-------------------------------------------------------------------------------------
## 목표
 - git 사용법 익힘
 - 개인 코딩 학습 습관화


-------------------------------------------------------------------------------------
## Visual Code 환경설정
#### 구성설정
* 설정
	* Editor > Hover > Delay : 2000 (300)

* 키맵 (keybindings.json)
	```json
	[
		{
			"key": "f3",
			"command": "editor.action.moveSelectionToNextFindMatch",
			"when": "editorFocus"
		},
		{
			"key": "alt+c",
			"command": "editor.action.toggleColumnSelection"
		},
		{
			"key": "f3",
			"command": "editor.action.nextSelectionMatchFindAction",
			"when": "editorTextFocus"
		}
	]
	```
	* 다음 선택 찾기 : F3 : editorTextFocus
	* 토글 컬럼 선택 : ALT+C

#### 프로젝트 설정
* 설정

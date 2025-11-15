# Git 分支操作详解

Git 分支是 Git 版本控制系统的核心功能之一，它允许开发者在不同的开发线上并行工作。下面将详细介绍几种主要的 Git 分支操作。

## 1. 基本分支操作

### 创建分支
```bash
git branch <分支名>
```


### 切换分支
```bash
git checkout <分支名>
# 或者
git switch <分支名>
```


### 创建并切换分支
```bash
git checkout -b <分支名>
# 或者
git switch -c <分支名>
```


## 2. 分支合并操作

### Merge（合并）
Merge 是将两个分支的历史合并在一起的操作，会创建一个新的合并提交。

初始状态：
```
      A1---A2---B1---B2 (yyc分支，其中A1,A2是A部分代码的提交，B1,B2是B部分代码的提交)
     /
D---E---F---G (main分支，其中F,G是B部分的新提交)
```


执行 merge 操作：
```bash
git checkout main
git merge yyc
```


合并后：
```
      A1---A2---B1---B2 (yyc分支)
     /                 \
D---E---F---G-----------M (main分支，M是合并提交)
```


### Rebase（变基）
Rebase 是将一个分支的提交"重放"到另一个分支的顶端，创建线性的提交历史。

初始状态：
```
      A1---A2---B1---B2 (yyc分支，其中A1,A2是A部分的提交，B1,B2是B部分的提交)
     /
D---E---F---G (main分支，其中F,G是B部分的新提交)
```


执行 rebase 操作：
```bash
git checkout yyc
git rebase main
```


变基后：
```
D---E---F---G---A1'---A2'---B1'---B2' (yyc分支，所有提交都基于main分支的最新提交)
```


注意：A1', A2', B1', B2' 是重新应用的提交，哈希值与原来不同，但内容相同。

## 3. 高级分支操作

### Cherry-pick（拣选）
Cherry-pick 允许你选择特定的提交并应用到当前分支。

例如，只想将 yyc 分支上的 A 部分提交应用到 main 分支：

```bash
git checkout main
git cherry-pick A1 A2
```


执行前：
```
      A1---A2---B1---B2 (yyc分支)
     /
D---E---F---G (main分支)
```


执行后：
```
      A1---A2---B1---B2 (yyc分支)
     /
D---E---F---G---A1'---A2' (main分支)
```


### Interactive Rebase（交互式变基）
交互式变基允许你在变基过程中修改、合并、删除或重新排序提交。

```bash
git rebase -i HEAD~4
```


这会打开一个编辑器，你可以选择：
- pick：使用该提交
- reword：修改提交信息
- edit：修改提交内容
- squash：与前一个提交合并
- drop：删除该提交

## 4. 分支管理操作

### 查看分支
```bash
# 查看本地分支
git branch

# 查看远程分支
git branch -r

# 查看所有分支
git branch -a
```


### 删除分支
```bash
# 删除本地分支
git branch -d <分支名>

# 强制删除本地分支
git branch -D <分支名>

# 删除远程分支
git push origin --delete <分支名>
```


### 重命名分支
```bash
# 重命名当前分支
git branch -m <新分支名>

# 重命名指定分支
git branch -m <旧分支名> <新分支名>
```


## 5. 实际应用场景

### 场景1：同步主分支的更新
当你在功能分支上工作时，主分支有了新的更新，你想获取这些更新：

```bash
git fetch origin
git rebase origin/main
```


### 场景2：将功能合并到主分支
完成功能开发后，将更改合并到主分支：

```bash
git checkout main
git merge feature-branch
```


或者使用变基方式：
```bash
git checkout feature-branch
git rebase main
git checkout main
git merge feature-branch
```


### 场景3：整理提交历史
在合并前整理功能分支的提交历史：

```bash
git rebase -i HEAD~n  # n是要整理的提交数量
```


## 6. 最佳实践

1. **功能分支工作流**：为每个新功能创建一个分支
2. **保持分支更新**：定期将主分支的更改同步到功能分支
3. **选择合适的合并策略**：
   - 在公共分支上使用 merge 保持历史完整性
   - 在个人分支上使用 rebase 保持历史整洁
4. **有意义的提交信息**：编写清晰、简洁的提交信息
5. **及时删除无用分支**：合并后删除已完成的功能分支

通过掌握这些分支操作，你可以更有效地管理代码版本，进行团队协作，并保持清晰的项目历史。
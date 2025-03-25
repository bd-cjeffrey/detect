def solve_n_queens(n):
    def is_safe(board, row, col):
        for i in range(col):
            if board[row][i] == 1:
                return False
        for i, j in zip(range(row, -1, -1), range(col, -1, -1)):
            if board[i][j] == 1:
                return False
        for i, j in zip(range(row, n), range(col, -1, -1)):
            if board[i][j] == 1:
                return False
        return True

    def solve(board, col):
        if col >= n:
            solutions.append(["".join("Q" if cell == 1 else "." for cell in row) for row in board])
            return True
        res = False
        for i in range(n):
            if is_safe(board, i, col):
                board[i][col] = 1
                res = solve(board, col + 1) or res
                board[i][col] = 0
        return res

    solutions = []
    board = [[0 for _ in range(n)] for _ in range(n)]
    solve(board, 0)
    return solutions


# Unit tests
import unittest

class TestNQueens(unittest.TestCase):
    def test_4_queens(self):
        solutions = solve_n_queens(4)
        expected = [
            [".Q..", "...Q", "Q...", "..Q."],
            ["..Q.", "Q...", "...Q", ".Q.."]
        ]
        self.assertEqual(len(solutions), 2)
        self.assertTrue(all(solution in expected for solution in solutions))

    def test_8_queens(self):
        solutions = solve_n_queens(8)
        self.assertEqual(len(solutions), 92)  # There are 92 solutions for 8 queens

    def test_no_solution(self):
        solutions = solve_n_queens(3)
        self.assertEqual(len(solutions), 0)  # No solution exists for 3 queens

if __name__ == "__main__":
    unittest.main()
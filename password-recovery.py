import hashlib
import secrets
import time
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from datetime import datetime, timedelta
import sqlite3
import re
import os
from typing import Optional, Tuple, Dict, List, Union

class PasswordRecoverySystem:
    def __init__(self, 
                 db_path: str = "password_recovery.db",
                 smtp_server: str = "smtp.gmail.com",
                 smtp_port: int = 587,
                 smtp_username: Optional[str] = None,
                 smtp_password: Optional[str] = None,
                 from_email: Optional[str] = None,
                 base_url: str = "http://localhost:3000"):
        """
        Initialize the Password Recovery System
        
        Args:
            db_path: Path to SQLite database
            smtp_server: SMTP server address
            smtp_port: SMTP server port
            smtp_username: SMTP username/email
            smtp_password: SMTP password or app password
            from_email: Sender email address
            base_url: Base URL for password reset links
        """
        self.db_path = db_path
        self.smtp_server = smtp_server
        self.smtp_port = smtp_port
        self.smtp_username = smtp_username or os.getenv('SMTP_USERNAME')
        self.smtp_password = smtp_password or os.getenv('SMTP_PASSWORD')
        self.from_email = from_email or self.smtp_username
        self.base_url = base_url
        self.init_database()
    
    def init_database(self):
        """Initialize the database with required tables"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # Users table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # Password reset tokens table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS reset_tokens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                token TEXT UNIQUE NOT NULL,
                expires_at TIMESTAMP NOT NULL,
                used BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users (id)
            )
        ''')
        
        conn.commit()
        conn.close()
        print("Database initialized successfully")
    
    def hash_password(self, password):
        """Hash a password using SHA-256 with salt"""
        salt = secrets.token_hex(16)
        password_hash = hashlib.sha256((password + salt).encode()).hexdigest()
        return f"{salt}:{password_hash}"
    
    def verify_password(self, password, stored_hash):
        """Verify a password against its hash"""
        try:
            salt, hash_value = stored_hash.split(':')
            password_hash = hashlib.sha256((password + salt).encode()).hexdigest()
            return password_hash == hash_value
        except ValueError:
            return False
    
    def validate_password_strength(self, password):
        """Validate password strength"""
        errors = []
        
        if len(password) < 8:
            errors.append("Password must be at least 8 characters long")
        
        if not re.search(r'[A-Z]', password):
            errors.append("Password must contain at least one uppercase letter")
        
        if not re.search(r'[a-z]', password):
            errors.append("Password must contain at least one lowercase letter")
        
        if not re.search(r'\d', password):
            errors.append("Password must contain at least one number")
        
        if not re.search(r'[^A-Za-z0-9]', password):
            errors.append("Password must contain at least one special character")
        
        return len(errors) == 0, errors
    
    def generate_reset_token(self):
        """Generate a secure reset token"""
        return secrets.token_urlsafe(32)
    
    def create_user(self, email, password):
        """Create a new user"""
        is_valid, errors = self.validate_password_strength(password)
        if not is_valid:
            return False, errors
        
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        try:
            password_hash = self.hash_password(password)
            cursor.execute(
                "INSERT INTO users (email, password_hash) VALUES (?, ?)",
                (email, password_hash)
            )
            conn.commit()
            user_id = cursor.lastrowid
            print(f"User created successfully with ID: {user_id}")
            return True, ["User created successfully"]
        except sqlite3.IntegrityError:
            return False, ["Email already exists"]
        finally:
            conn.close()
    
    def request_password_reset(self, email):
        """Request a password reset for a user"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # Check if user exists
        cursor.execute("SELECT id FROM users WHERE email = ?", (email,))
        user = cursor.fetchone()
        
        if not user:
            conn.close()
            return False, "Email not found"
        
        user_id = user[0]
        
        # Generate reset token
        token = self.generate_reset_token()
        expires_at = datetime.now() + timedelta(hours=1)  # Token expires in 1 hour
        
        # Store reset token
        cursor.execute(
            "INSERT INTO reset_tokens (user_id, token, expires_at) VALUES (?, ?, ?)",
            (user_id, token, expires_at)
        )
        conn.commit()
        conn.close()
        
        # Send email (simulated)
        self.send_reset_email(email, token)
        
        print(f"Password reset requested for {email}")
        print(f"Reset token: {token}")
        return True, "Password reset email sent"
    
    def send_reset_email(self, email: str, token: str) -> bool:
        """
        Send password reset email using configured SMTP server
        
        Args:
            email: Recipient email address
            token: Password reset token
            
        Returns:
            bool: True if email sent successfully, False otherwise
        """
        reset_url = f"{self.base_url}/auth/reset-password?token={token}"
        
        # Create message
        msg = MIMEMultipart()
        msg['From'] = self.from_email
        msg['To'] = email
        msg['Subject'] = "Password Reset Request"
        
        body = f"""
        Hello,
        
        You have requested to reset your password. Click the following link to proceed:
        
        {reset_url}
        
        This link will expire in 1 hour.
        
        If you did not request this password reset, please ignore this email.
        
        Best regards,
        Your Application Team
        """
        
        msg.attach(MIMEText(body, 'plain'))
        
        try:
            # Connect to SMTP server
            server = smtplib.SMTP(self.smtp_server, self.smtp_port)
            server.starttls()
            
            # Login if credentials provided
            if self.smtp_username and self.smtp_password:
                server.login(self.smtp_username, self.smtp_password)
            
            # Send email
            server.send_message(msg)
            server.quit()
            print(f"Password reset email sent successfully to {email}")
            return True
            
        except Exception as e:
            print(f"Failed to send password reset email: {str(e)}")
            return False
    
    def validate_reset_token(self, token):
        """Validate a reset token"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute('''
            SELECT rt.id, rt.user_id, rt.expires_at, rt.used, u.email
            FROM reset_tokens rt
            JOIN users u ON rt.user_id = u.id
            WHERE rt.token = ?
        ''', (token,))
        
        result = cursor.fetchone()
        conn.close()
        
        if not result:
            return False, "Invalid token"
        
        token_id, user_id, expires_at, used, email = result
        
        if used:
            return False, "Token has already been used"
        
        expires_at = datetime.fromisoformat(expires_at)
        if datetime.now() > expires_at:
            return False, "Token has expired"
        
        return True, {"token_id": token_id, "user_id": user_id, "email": email}
    
    def reset_password(self, token, new_password, confirm_password):
        """Reset password using a valid token"""
        if new_password != confirm_password:
            return False, "Passwords do not match"
        
        is_valid, errors = self.validate_password_strength(new_password)
        if not is_valid:
            return False, errors
        
        is_valid_token, token_data = self.validate_reset_token(token)
        if not is_valid_token:
            return False, token_data
        
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        try:
            # Update user password
            new_password_hash = self.hash_password(new_password)
            cursor.execute(
                "UPDATE users SET password_hash = ? WHERE id = ?",
                (new_password_hash, token_data["user_id"])
            )
            
            # Mark token as used
            cursor.execute(
                "UPDATE reset_tokens SET used = TRUE WHERE id = ?",
                (token_data["token_id"],)
            )
            
            conn.commit()
            print(f"Password reset successfully for {token_data['email']}")
            return True, "Password reset successfully"
        
        except Exception as e:
            conn.rollback()
            return False, f"Error resetting password: {str(e)}"
        finally:
            conn.close()
    
    def cleanup_expired_tokens(self):
        """Clean up expired tokens"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute(
            "DELETE FROM reset_tokens WHERE expires_at < ? OR used = TRUE",
            (datetime.now(),)
        )
        
        deleted_count = cursor.rowcount
        conn.commit()
        conn.close()
        
        print(f"Cleaned up {deleted_count} expired/used tokens")
        return deleted_count

# Demo usage
if __name__ == "__main__":
    # Initialize the password recovery system with email configuration
    prs = PasswordRecoverySystem()
    
    # Create a demo user
    print("=== Creating Demo User ===")
    success, message = prs.create_user("user@example.com", "SecurePass123!")
    print(f"Result: {success}, Message: {message}")
    
    # Request password reset
    print("\n=== Requesting Password Reset ===")
    success, message = prs.request_password_reset("user@example.com")
    print(f"Result: {success}, Message: {message}")
    
    # Simulate token validation (you would get this from the email link)
    print("\n=== Validating Token ===")
    conn = sqlite3.connect(prs.db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT token FROM reset_tokens WHERE used = FALSE ORDER BY created_at DESC LIMIT 1")
    latest_token = cursor.fetchone()
    conn.close()
    
    if latest_token:
        token = latest_token[0]
        is_valid, result = prs.validate_reset_token(token)
        print(f"Token valid: {is_valid}, Result: {result}")
        
        # Reset password
        print("\n=== Resetting Password ===")
        success, message = prs.reset_password(token, "NewSecurePass456!", "NewSecurePass456!")
        print(f"Result: {success}, Message: {message}")
    
    # Clean up expired tokens
    print("\n=== Cleaning Up Tokens ===")
    cleaned = prs.cleanup_expired_tokens()

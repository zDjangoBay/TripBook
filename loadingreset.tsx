import React, { useState, useEffect } from 'react';
import { Key, Loader2, CheckCircle, AlertCircle, Eye, EyeOff } from 'lucide-react';
import { useRouter } from 'next/router';
import axios from 'axios';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'default' | 'ghost' | 'destructive';
  size?: 'default' | 'sm' | 'lg';
  className?: string;
}

const Button: React.FC<ButtonProps> = ({
  children,
  className = '',
  variant = 'default',
  size = 'default',
  disabled = false,
  type = 'button',
  onClick,
  ...props
}) => {
  const baseClasses = 'inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none';
  const variants = {
    default: 'bg-blue-600 text-white hover:bg-blue-700',
    ghost: 'hover:bg-gray-100',
    destructive: 'bg-red-600 text-white hover:bg-red-700'
  };
  const sizes = {
    default: 'h-10 py-2 px-4',
    sm: 'h-9 px-3',
    lg: 'h-11 px-8'
  };
  
  return (
    <button
      className={`${baseClasses} ${variants[variant]} ${sizes[size]} ${className}`}
      disabled={disabled}
      type={type}
      onClick={onClick}
      {...props}
    >
      {children}
    </button>
  );
};

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  className?: string;
}

const Input: React.FC<InputProps> = ({ className = '', type = 'text', disabled = false, ...props }) => {
  return (
    <input
      type={type}
      className={`flex h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm ring-offset-white file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-gray-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-600 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${className}`}
      disabled={disabled}
      {...props}
    />
  );
};

interface LabelProps {
  children: React.ReactNode;
  htmlFor?: string;
  className?: string;
}

const Label: React.FC<LabelProps> = ({ children, htmlFor, className = '' }) => {
  return (
    <label
      htmlFor={htmlFor}
      className={`text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 ${className}`}
    >
      {children}
    </label>
  );
};

interface CardProps {
  children: React.ReactNode;
  className?: string;
}

const Card: React.FC<CardProps> = ({ children, className = '' }) => {
  return (
    <div className={`rounded-lg border border-gray-200 bg-white text-gray-950 shadow-sm ${className}`}>
      {children}
    </div>
  );
};

interface CardHeaderProps {
  children: React.ReactNode;
  className?: string;
}

const CardHeader: React.FC<CardHeaderProps> = ({ children, className = '' }) => {
  return (
    <div className={`flex flex-col space-y-1.5 p-6 ${className}`}>
      {children}
    </div>
  );
};

interface CardTitleProps {
  children: React.ReactNode;
  className?: string;
}

const CardTitle: React.FC<CardTitleProps> = ({ children, className = '' }) => {
  return (
    <h3 className={`text-2xl font-semibold leading-none tracking-tight ${className}`}>
      {children}
    </h3>
  );
};

interface CardDescriptionProps {
  children: React.ReactNode;
  className?: string;
}

const CardDescription: React.FC<CardDescriptionProps> = ({ children, className = '' }) => {
  return (
    <p className={`text-sm text-gray-500 ${className}`}>
      {children}
    </p>
  );
};

interface CardContentProps {
  children: React.ReactNode;
  className?: string;
}

const CardContent: React.FC<CardContentProps> = ({ children, className = '' }) => {
  return (
    <div className={`p-6 pt-0 ${className}`}>
      {children}
    </div>
  );
};

interface AlertProps {
  children: React.ReactNode;
  variant?: 'default' | 'destructive';
  className?: string;
}

const Alert: React.FC<AlertProps> = ({ children, variant = 'default', className = '' }) => {
  const variants = {
    default: 'border-gray-200 text-gray-950',
    destructive: 'border-red-200 text-red-800 bg-red-50'
  };
  
  return (
    <div className={`relative w-full rounded-lg border p-4 ${variants[variant]} ${className}`}>
      {children}
    </div>
  );
};

interface AlertDescriptionProps {
  children: React.ReactNode;
  className?: string;
}

const AlertDescription: React.FC<AlertDescriptionProps> = ({ children, className = '' }) => {
  return (
    <div className={`text-sm ${className}`}>
      {children}
    </div>
  );
};

interface ProgressProps {
  value?: number;
  className?: string;
  max?: number;
}

const Progress: React.FC<ProgressProps> = ({ value = 0, className = '', max = 100 }) => {
  const percentage = Math.min(Math.max(value, 0), max);
  
  return (
    <div className={`relative h-4 w-full overflow-hidden rounded-full bg-gray-200 ${className}`}>
      <div
        className="h-full bg-blue-600 transition-all duration-300 ease-in-out"
        style={{ width: `${(percentage / max) * 100}%` }}
      />
    </div>
  );
};

interface PasswordStrength {
  score: number;
  feedback: string[];
}

export default function ResetPasswordPage() {
  const router = useRouter();
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [error, setError] = useState('');
  const [token, setToken] = useState<string | null>(null);
  const [isValidatingToken, setIsValidatingToken] = useState(true);
  const [passwordStrength, setPasswordStrength] = useState<PasswordStrength>({ score: 0, feedback: [] });

  useEffect(() => {
    const resetToken = router.query.token as string;
    if (!resetToken) {
      setError('Invalid or missing reset token');
      setIsValidatingToken(false);
      return;
    }

    const validateToken = async () => {
      try {
        const response = await axios.post('/api/auth/validate-reset-token', { token: resetToken });
        if (response.data.valid) {
          setToken(resetToken);
        } else {
          setError('Invalid or expired reset token');
        }
      } catch (err) {
        setError('Failed to validate reset token');
      } finally {
        setIsValidatingToken(false);
      }
    };

    validateToken();
  }, [router.query.token]);

  const calculatePasswordStrength = (pwd: string): PasswordStrength => {
    let score = 0;
    const feedback: string[] = [];

    if (pwd.length >= 8) score += 20;
    else feedback.push('At least 8 characters');

    if (/[A-Z]/.test(pwd)) score += 20;
    else feedback.push('One uppercase letter');

    if (/[a-z]/.test(pwd)) score += 20;
    else feedback.push('One lowercase letter');

    if (/\d/.test(pwd)) score += 20;
    else feedback.push('One number');

    if (/[^A-Za-z0-9]/.test(pwd)) score += 20;
    else feedback.push('One special character');

    return { score: Math.min(score, 100), feedback };
  };

  useEffect(() => {
    if (password) {
      setPasswordStrength(calculatePasswordStrength(password));
    } else {
      setPasswordStrength({ score: 0, feedback: [] });
    }
  }, [password]);

  const getStrengthColor = (score: number) => {
    if (score < 40) return 'bg-red-500';
    if (score < 60) return 'bg-yellow-500';
    if (score < 80) return 'bg-blue-500';
    return 'bg-green-500';
  };

  const getStrengthText = (score: number) => {
    if (score < 20) return 'Very Weak';
    if (score < 40) return 'Weak';
    if (score < 60) return 'Fair';
    if (score < 80) return 'Good';
    if (score < 100) return 'Strong';
    return 'Very Strong';
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      setIsLoading(false);
      return;
    }

    if (passwordStrength.score < 40) {
      setError('Password is too weak. Please choose a stronger password.');
      setIsLoading(false);
      return;
    }

    try {
      const response = await axios.post('/api/auth/reset-password', {
        token,
        newPassword: password
      });

      if (response.data.success) {
        setIsSuccess(true);
      } else {
        setError(response.data.message || 'Failed to reset password');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to reset password. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleBackToSignIn = () => {
    router.push('/auth/signin');
  };

  if (isValidatingToken) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
        <Card className="w-full max-w-md">
          <CardContent className="pt-6">
            <div className="text-center">
              <Loader2 className="mx-auto h-8 w-8 animate-spin text-blue-600" />
              <p className="mt-2 text-sm text-gray-600">Validating reset token...</p>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (isSuccess) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <div className="mx-auto mb-4 w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
              <CheckCircle className="h-6 w-6 text-green-600" />
            </div>
            <CardTitle>Password Reset Successful</CardTitle>
            <CardDescription>Your password has been successfully updated</CardDescription>
          </CardHeader>
          <CardContent>
            <Button className="w-full" onClick={handleBackToSignIn}>
              Sign In with New Password
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <div className="mx-auto mb-4 w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
            <Key className="h-6 w-6 text-blue-600" />
          </div>
          <CardTitle className="text-center">Reset Your Password</CardTitle>
          <CardDescription className="text-center">Enter your new password below</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="password">New Password</Label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Enter new password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  disabled={isLoading}
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4 text-gray-400" />
                  ) : (
                    <Eye className="h-4 w-4 text-gray-400" />
                  )}
                </Button>
              </div>

              {password && (
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span>Password Strength:</span>
                    <span
                      className={`font-medium ${
                        passwordStrength.score < 40
                          ? 'text-red-600'
                          : passwordStrength.score < 60
                            ? 'text-yellow-600'
                            : passwordStrength.score < 80
                              ? 'text-blue-600'
                              : 'text-green-600'
                      }`}
                    >
                      {getStrengthText(passwordStrength.score)}
                    </span>
                  </div>
                  <div className="relative w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                    <div
                      className={`h-full transition-all duration-300 ${getStrengthColor(passwordStrength.score)}`}
                      style={{ width: `${passwordStrength.score}%` }}
                    />
                  </div>
                  {passwordStrength.feedback.length > 0 && (
                    <div className="text-xs text-gray-600">
                      <p>Missing:</p>
                      <ul className="list-disc list-inside">
                        {passwordStrength.feedback.map((item, index) => (
                          <li key={index}>{item}</li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirm New Password</Label>
              <div className="relative">
                <Input
                  id="confirmPassword"
                  type={showConfirmPassword ? 'text' : 'password'}
                  placeholder="Confirm new password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  disabled={isLoading}
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                >
                  {showConfirmPassword ? (
                    <EyeOff className="h-4 w-4 text-gray-400" />
                  ) : (
                    <Eye className="h-4 w-4 text-gray-400" />
                  )}
                </Button>
              </div>

              {confirmPassword && password !== confirmPassword && (
                <p className="text-xs text-red-600">Passwords do not match</p>
              )}
            </div>

            {error && (
              <Alert variant="destructive">
                <div className="flex items-center">
                  <AlertCircle className="h-4 w-4 mr-2" />
                  <AlertDescription>{error}</AlertDescription>
                </div>
              </Alert>
            )}

            <Button
              type="submit"
              className="w-full"
              disabled={isLoading || password !== confirmPassword || passwordStrength.score < 40}
            >
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Updating Password...
                </>
              ) : (
                'Reset Password'
              )}
            </Button>

            <div className="mt-4 text-center">
              <button
                type="button"
                onClick={handleBackToSignIn}
                className="text-sm text-blue-600 hover:underline bg-transparent border-none cursor-pointer"
              >
                Back to Sign In
              </button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
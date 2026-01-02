/**
 * Форматирует номер телефона в формат +7 XXX XXX XX XX
 * Принимает номер в любом формате (89372867888, +79372867888, 9372867888)
 * Возвращает отформатированный номер +7 937 286 78 88
 */
export function formatPhoneNumber(value: string): string {
  // Удаляем все нецифровые символы
  const digits = value.replace(/\D/g, '');
  
  // Если номер начинается с 8, заменяем на 7
  let phone = digits.startsWith('8') ? '7' + digits.slice(1) : digits;
  
  // Если номер начинается с 7, оставляем как есть
  // Если номер не начинается с 7, добавляем 7
  if (!phone.startsWith('7') && phone.length > 0) {
    phone = '7' + phone;
  }
  
  // Ограничиваем длину до 11 цифр (7 + 10 цифр)
  phone = phone.slice(0, 11);
  
  // Форматируем: +7 XXX XXX XX XX
  if (phone.length === 0) {
    return '';
  }
  
  if (phone.length <= 1) {
    return `+${phone}`;
  }
  
  if (phone.length <= 4) {
    return `+${phone.slice(0, 1)} ${phone.slice(1)}`;
  }
  
  if (phone.length <= 7) {
    return `+${phone.slice(0, 1)} ${phone.slice(1, 4)} ${phone.slice(4)}`;
  }
  
  if (phone.length <= 9) {
    return `+${phone.slice(0, 1)} ${phone.slice(1, 4)} ${phone.slice(4, 7)} ${phone.slice(7)}`;
  }
  
  return `+${phone.slice(0, 1)} ${phone.slice(1, 4)} ${phone.slice(4, 7)} ${phone.slice(7, 9)} ${phone.slice(9)}`;
}

/**
 * Извлекает только цифры из номера телефона
 */
export function getPhoneDigits(phone: string): string {
  return phone.replace(/\D/g, '');
}

/**
 * Проверяет, является ли номер телефона валидным российским номером
 */
export function isValidRussianPhone(phone: string): boolean {
  const digits = getPhoneDigits(phone);
  // Российский номер: 7 + 10 цифр = 11 цифр
  // Или 8 + 10 цифр = 11 цифр
  if (digits.length !== 11) {
    return false;
  }
  
  // Должен начинаться с 7 или 8
  return digits.startsWith('7') || digits.startsWith('8');
}

/**
 * Нормализует номер телефона для сохранения в БД
 * Преобразует в формат +7 XXX XXX XX XX
 */
export function normalizePhoneNumber(phone: string): string {
  const digits = getPhoneDigits(phone);
  
  if (digits.length === 0) {
    return '';
  }
  
  // Если начинается с 8, заменяем на 7
  let normalized = digits.startsWith('8') ? '7' + digits.slice(1) : digits;
  
  // Если не начинается с 7, добавляем 7
  if (!normalized.startsWith('7') && normalized.length > 0) {
    normalized = '7' + normalized;
  }
  
  // Ограничиваем до 11 цифр
  normalized = normalized.slice(0, 11);
  
  // Форматируем для сохранения
  if (normalized.length === 11) {
    return `+${normalized.slice(0, 1)} ${normalized.slice(1, 4)} ${normalized.slice(4, 7)} ${normalized.slice(7, 9)} ${normalized.slice(9)}`;
  }
  
  return formatPhoneNumber(normalized);
}



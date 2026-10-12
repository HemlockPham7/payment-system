import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'
import dayjs from 'dayjs'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('MMMM DD, YYYY')
}

export function parseMarkdownToJson(markdownText: string): unknown | null {
  const regex = /```json\n([\s\S]+?)\n```/
  const match = markdownText.match(regex)

  if (match && match[1]) {
    try {
      return JSON.parse(match[1])
    } catch (error) {
      console.error('Error parsing JSON:', error)
      return null
    }
  }
  console.error('No valid JSON found in markdown text.')
  return null
}

export const formatCount = (count: number) => {
  return new Intl.NumberFormat('en', {
    notation: 'compact',
    maximumFractionDigits: 1,
  }).format(count)
}

export const formatTimeAgo = (createdAt: string) => {
  const createdTime = new Date(createdAt).getTime()
  const now = Date.now()
  const diff = Math.max(0, now - createdTime)

  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes} minutes ago`
  if (hours < 24) return `${hours} hours ago`
  if (days < 30) return `${days} days ago`

  const months = Math.floor(days / 30)
  if (months < 12) return `${months} months ago`

  return `${Math.floor(days / 365)} years ago`
}

import { useTheme } from '@mui/material/styles';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { SparkLineChart } from '@mui/x-charts/SparkLineChart';
import { areaElementClasses } from '@mui/x-charts/LineChart';

export type StatCardProps = {
  title: string;
  value: string;
  interval: string;
  data: number[];
};

function getDaysInMonth(month: number, year: number) {
  const date = new Date(year, month, 0);
  const monthName = date.toLocaleDateString('en-US', {
    month: 'short',
  });
  const daysInMonth = date.getDate();
  const days = [];
  let i = 1;
  while (days.length < daysInMonth) {
    days.push(`${monthName} ${i}`);
    i += 1;
  }
  return days;
}

function AreaGradient({ color, id }: { color: string; id: string }) {
  return (
    <defs>
      <linearGradient id={id} x1="50%" y1="0%" x2="50%" y2="100%">
        <stop offset="0%" stopColor={color} stopOpacity={0.3} />
        <stop offset="100%" stopColor={color} stopOpacity={0} />
      </linearGradient>
    </defs>
  );
}

type TrendType = 'up' | 'down' | 'neutral';

function calculateTrend(data: number[]): { trend: TrendType; value: string } {
  if (data.length < 2) {
    return { trend: 'neutral', value: '0%' };
  }

  // Tính giá trị trung bình của toàn bộ dữ liệu
  const average = data.reduce((sum, val) => sum + val, 0) / data.length;
  const lastValue = data[data.length - 1];
  
  // Tính phần trăm thay đổi giữa giá trị cuối và giá trị trung bình
  const percentChange = ((lastValue - average) / average) * 100;
  
  // Làm tròn đến 1 chữ số thập phân
  const roundedPercent = Math.round(percentChange * 10) / 10;
  
  // Xác định xu hướng dựa trên mức độ thay đổi
  let trend: TrendType = 'neutral';
  if (roundedPercent > 10) trend = 'up';
  else if (roundedPercent < -10) trend = 'down';
  
  return {
    trend,
    value: `${roundedPercent > 0 ? '+' : ''}${roundedPercent}%`,
  };
}

function getTrendColor(theme: any, trend: TrendType) {
  const baseColors = {
    up: theme.palette.mode === 'light' ? theme.palette.success.main : theme.palette.success.dark,
    down: theme.palette.mode === 'light' ? theme.palette.error.main : theme.palette.error.dark,
    neutral: theme.palette.mode === 'light' ? theme.palette.grey[400] : theme.palette.grey[700],
  };

  if (trend === 'neutral') return baseColors.neutral;

  // Điều chỉnh độ sáng dựa trên cường độ
  const color = baseColors[trend];
  return color;
}

export default function StatCard({
  title,
  value,
  interval,
  data,
}: StatCardProps) {
  const theme = useTheme();
  const daysInWeek = getDaysInMonth(4, 2024);

  const { trend, value: trendValue } = calculateTrend(data);
  const chartColor = getTrendColor(theme, trend);

  const labelColors = {
    up: 'success' as const,
    down: 'error' as const,
    neutral: 'default' as const,
  };

  return (
    <Card variant="outlined" sx={{ height: '100%', flexGrow: 1 }}>
      <CardContent>
        <Typography component="h2" variant="subtitle2" gutterBottom>
          {title}
        </Typography>
        <Stack
          direction="column"
          sx={{ justifyContent: 'space-between', flexGrow: '1', gap: 1 }}
        >
          <Stack sx={{ justifyContent: 'space-between' }}>
            <Stack
              direction="row"
              sx={{ justifyContent: 'space-between', alignItems: 'center' }}
            >
              <Typography variant="h4" component="p">
                {value}
              </Typography>
              <Chip size="small" color={labelColors[trend]} label={trendValue} />
            </Stack>
            <Typography variant="caption" sx={{ color: 'text.secondary' }}>
              {interval}
            </Typography>
          </Stack>
          <Box sx={{ width: '100%', height: 50 }}>
            <SparkLineChart
              colors={[chartColor]}
              data={data}
              area
              showHighlight
              showTooltip
              xAxis={{
                scaleType: 'band',
                data: daysInWeek,
              }}
              sx={{
                [`& .${areaElementClasses.root}`]: {
                  fill: `url(#area-gradient-${value})`,
                },
              }}
            >
              <AreaGradient color={chartColor} id={`area-gradient-${value}`} />
            </SparkLineChart>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
} 
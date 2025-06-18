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

function getLast30Days() {
  const days = [];
  const today = new Date();
  
  for (let i = 29; i >= 0; i--) {
    const date = new Date(today);
    date.setDate(today.getDate() - i);
    
    const day = date.getDate();
    const month = date.toLocaleDateString('en-US', {
      month: 'short',
    });
    
    days.push(`${month} ${day}`);
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

  // So sánh 2 giá trị cuối cùng (ngày mới nhất và ngày trước đó)
  const lastValue = data[data.length - 1];
  const prevValue = data[data.length - 2];
  
  let percentChange: number;
  
  if (prevValue === 0) {
    // Nếu giá trị trước đó là 0, thì phần trăm thay đổi là 100% nếu giá trị hiện tại > 0
    percentChange = lastValue > 0 ? 100 : 0;
  } else {
    percentChange = ((lastValue - prevValue) / prevValue) * 100;
  }
  
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
  const last30Days = getLast30Days();

  const { trend: calculatedTrend, value: trendValue } = calculateTrend(data);
  const chartColor = getTrendColor(theme, calculatedTrend);

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
              <Chip size="small" color={labelColors[calculatedTrend]} label={trendValue} />
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
                data: last30Days,
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
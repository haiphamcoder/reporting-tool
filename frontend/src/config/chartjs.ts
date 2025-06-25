import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    ArcElement,
    LineElement,
    PointElement,
    Filler,
    RadialLinearScale,
    DoughnutController,
    BarController,
    LineController,
    PieController
} from 'chart.js';

// Register all Chart.js components once
ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    ArcElement,
    LineElement,
    PointElement,
    Filler,
    RadialLinearScale,
    DoughnutController,
    BarController,
    LineController,
    PieController
);

export default ChartJS; 
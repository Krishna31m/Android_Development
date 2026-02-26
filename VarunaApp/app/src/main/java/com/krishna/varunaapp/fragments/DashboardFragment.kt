package com.krishna.varunaapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.krishna.varunaapp.R
import com.krishna.varunaapp.activities.AlertActivity
import com.krishna.varunaapp.activities.InformationActivity
import com.krishna.varunaapp.activities.HealthReportsListActivity
import com.krishna.varunaapp.activities.VillageListActivity
import com.krishna.varunaapp.databinding.FragmentDashboardBinding
import com.krishna.varunaapp.activities.WaterReferenceActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        setupClicks()
        return binding.root
    }

    private fun setupClicks() {

        // -------- HEALTH REPORTS -----------
//        binding.cardHealthReports.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_dashboardFragment_to_helpRequestsFragment
//            )
//        }
        binding.cardHealthReports.setOnClickListener {
            startActivity(Intent(requireContext(), VillageListActivity::class.java))
        }


        // -------- WATER QUALITY -----------
        binding.cardWaterQuality.setOnClickListener {
            findNavController().navigate(
                R.id.action_dashboardFragment_to_waterQualityFragment
            )
        }

        // -------- ALERTS (Open Activity) -----------
        binding.cardAlerts.setOnClickListener {
            val intent = Intent(requireContext(), AlertActivity::class.java)
            startActivity(intent)
        }

        // -------- EDUCATION (Optional later) -----------
        binding.cardEducation.setOnClickListener {
            val intent = Intent(requireContext(), InformationActivity::class.java)
            startActivity(intent)
        }

//        Water csv file format information
        binding.cardWaterReference.setOnClickListener {
            val intent = Intent(requireContext(), WaterReferenceActivity::class.java)
            startActivity(intent)
        }

        // chat bot
//        binding.cardChatBotRefrence.setOnClickListener {
//            val intent = Intent(requireContext(), ChatActivity::class.java)
//            startActivity(intent)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
